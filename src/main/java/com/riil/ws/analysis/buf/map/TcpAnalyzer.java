package com.riil.ws.analysis.buf.map;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author GlobalZ
 */
@Service
public class TcpAnalyzer {
    private final Logger LOGGER = LoggerFactory.getLogger(TcpAnalyzer.class);

    public void analysis(TcpStream tcpStream) throws Exception {

        List<Frame> frames = tcpStream.getFrames();
        for (Frame frame : frames) {
            FrameJsonObject json = frame.toJsonObject();
            realTimeCreateConn(json, tcpStream);
            httpDelay(json, tcpStream);
            frame.setFrameJson(json);
        }

        endCreateConn(tcpStream);

        onlySuccess(tcpStream);
    }

    /**
     * 实时建连阶段分析。从有syn数据开始分析，能够分析出成功和rst的情况。
     * 此分析过程不能判断建连无响应的情况，建连无响应的状态是在一个tcpStream结束时判断的。
     * 建连成功状态会在分析过程中设置或者清楚：当分析出syn+ack的ack时，设置建连成功状态，当遇到syn重传或者syn+ack重传时，清除建连成功状态。
     * 该方法会在一个tcpStream的过程中一直被调用，因为目前还不能准确判断出数据传输是什么时候开始。
     * 业务定义（客户端建连时延、服务端建连时延）与ws的ack_frame不同。有些场景，不能使用ack_frame直接匹配syn+ack，需要将syn+ack的number存储起来，查找是否包含ack_frame
     *
     * @param json
     * @param tcpStream
     */
    private void realTimeCreateConn(FrameJsonObject json, TcpStream tcpStream) {
        if (json.isTcpConnectionSyn()) { // syn包
            tcpStream.setHasSyn(true);

            // 只记录第一个syn包的时间
            if (tcpStream.getSynTimeStamp() == null) {
                tcpStream.setSynTimeStamp(json.getTimeStamp());
                tcpStream.setClientIp(json.getSrcIp());
                tcpStream.setServerIp(json.getDstIp());
            }

            // 可能发生了syn重传，清除建连成功的标志
            clearTcpConnSuccess(tcpStream);
        } else if (json.isTcpConnectionSack()) { // syn+ack包
            if (tcpStream.getClientIp() == null && tcpStream.getServerIp() == null) {
                // 丢失syn包的时候，纠正下客户端和服务端ip
                tcpStream.setClientIp(json.getDstIp());
                tcpStream.setServerIp(json.getSrcIp());
            }
            tcpStream.addSackFrameNumber(json.getFrameNumber());
            tcpStream.setSackTimeStamp(json.getTimeStamp());

            // 可能发生了syn+ack 重传，清除建连成功的标志
            clearTcpConnSuccess(tcpStream);
        } else if (json.isTcpConnectionRst()) { // rst包
            if (tcpStream.getTcpConnectionSuccess() == null) {
                if (!tcpStream.hasSyn()) {
                    LOGGER.warn("TcpStream: " + tcpStream.getTcpStreamNumber() + ", ifHasSyn=" + tcpStream.hasSyn()
                            + ", Can't judge client or server connection rst");
                } else {
                    // RST建连失败
                    tcpStream.setTcpConnectionSuccess(Boolean.FALSE);
                    json.setClientIp(tcpStream.getClientIp());
                    json.setServerIp(tcpStream.getServerIp());
                    if (json.getSrcIp().equals(tcpStream.getClientIp())) { // 客户端建连RST
                        json.setTcpClientConnectionRst();
                    } else if (json.getSrcIp().equals(tcpStream.getServerIp())) { // 服务端建连RST
                        json.setTcpServerConnectionRst();
                    }
                }
            }
        } else if (json.getTcpAckFrameNumber() != null // syn+ack包的ack包
                && tcpStream.hasSackFrameNumber(json.getTcpAckFrameNumber())) {
            if (tcpStream.hasSyn()) {
                markTcpConnSuccess(tcpStream, json);
            }
        }
    }

    private void httpDelay(FrameJsonObject frame, TcpStream tcpStream) {
        httpReqTransDelay(frame, tcpStream);
        httpRespAndRespTransDelay(frame, tcpStream);
    }

    private void markTcpConnSuccess(TcpStream tcpStream, FrameJsonObject frame) {
        tcpStream.setTcpConnAckTimeStamp(frame.getTimeStamp());
        tcpStream.setTcpConnAckFrameNumber(frame.getFrameNumber());

        // 建连成功，如果发生syn或者syn+ack的重传，这个标志会被清除
        frame.setTcpConnectionSuccess();
        frame.setClientIp(tcpStream.getClientIp());
        frame.setServerIp(tcpStream.getServerIp());
        tcpStream.setTcpConnectionSuccess(Boolean.TRUE);
    }

    /**
     * 遇到syn和syn+ack时被调用，如果已经是建连成功的，清除建连成功状态。
     *
     * @param tcpStream
     */
    private void clearTcpConnSuccess(TcpStream tcpStream) {
        if (Boolean.TRUE.equals(tcpStream.getTcpConnectionSuccess())) {
            Frame frame = MapCache.getFrame(tcpStream.getTcpConnAckFrameNumber());
            FrameJsonObject json = frame.toJsonObject();
            json.delClientIp();
            json.delServerIp();
            json.delTcpConnectionSuccess();
            frame.setFrameJson(json);
            tcpStream.setTcpConnectionSuccess(null);
            tcpStream.setTcpConnAckFrameNumber(null);
            tcpStream.setTcpConnAckTimeStamp(null);
        }
    }

    private void httpReqTransDelay(FrameJsonObject json, TcpStream tcpStream) {
        if (!json.isHttpRequest()) {
            return;
        }

        Integer firstSegment = json.getFirstTcpSegmentIfHas();
        if (firstSegment == null) {
            json.setHttpReqTransDelay(FrameConstant.ZERO_STRING);
        } else {
            Long delay = Long.valueOf(json.getTimeStamp())
                    - Long.valueOf(MapCache.getFrame(firstSegment).toJsonObject().getTimeStamp());
            json.setHttpReqTransDelay(String.valueOf(delay));
        }
    }

    private void httpRespAndRespTransDelay(FrameJsonObject json, TcpStream tcpStream) {
        if (!json.isHttpResponse()) {
            return;
        }

        Integer httpRequestIn = json.getHttpRequestIn();
        if (httpRequestIn == null) {
            LOGGER.warn("TcpStream=" + tcpStream.getTcpStreamNumber() + ", frameNumber=" + json.getFrameNumber() + ", httpRequestIn is null.");
        }
        Integer firstSegment = json.getFirstTcpSegmentIfHas();
        if (firstSegment == null) {
            if (httpRequestIn != null) {
                Long delay = Long.valueOf(json.getTimeStamp())
                        - Long.valueOf(MapCache.getFrame(httpRequestIn).toJsonObject().getTimeStamp());
                json.setHttpRespDelay(String.valueOf(delay));
            }
            json.setHttpRespTransDelay(FrameConstant.ZERO_STRING);
        } else {
            if (httpRequestIn != null) {
                Long respDelay = Long.valueOf(MapCache.getFrame(firstSegment).toJsonObject().getTimeStamp())
                        - Long.valueOf(MapCache.getFrame(httpRequestIn).toJsonObject().getTimeStamp());
                json.setHttpRespDelay(String.valueOf(respDelay));
            }
            Long respTransDelay = Long.valueOf(json.getTimeStamp())
                    - Long.valueOf(MapCache.getFrame(firstSegment).toJsonObject().getTimeStamp());
            json.setHttpRespTransDelay(String.valueOf(respTransDelay));
        }
    }

    /**
     * 结束时建连状态分析，判断建连无响应的情况，判断一个tcpStream是否有syn包。
     * <p>
     * 结束时建连时延分析，判断出客户端建连时延、服务端建连时延、总建连时延
     *
     * @param tcpStream
     */
    private void endCreateConn(TcpStream tcpStream) {

        if (tcpStream.getTcpConnectionSuccess() == null) {
            if (!tcpStream.hasSyn()) {
                LOGGER.warn("TcpStream: " + tcpStream.getTcpStreamNumber() + ", ifHasSyn=" + tcpStream.hasSyn()
                        + ", can't judge client or server no response.");
            } else {
                List<Frame> frames = tcpStream.getFrames();
                Frame lastFrame = frames.get(frames.size() - 1);
                FrameJsonObject json = lastFrame.toJsonObject();
                json.setClientIp(tcpStream.getClientIp());
                json.setServerIp(tcpStream.getServerIp());
                if (tcpStream.getSynTimeStamp() != null && tcpStream.getSackTimeStamp() != null) {
                    json.setTcpClientConnectionNoResp();
                } else if (tcpStream.getSynTimeStamp() != null) {
                    json.setTcpServerConnectionNoResp();
                }
                lastFrame.setFrameJson(json);
            }
        } else if (Boolean.TRUE.equals(tcpStream.getTcpConnectionSuccess())) { // 建连成功时，计算建连时延
            long clientConnDelay = tcpStream.getSackTimeStamp() - tcpStream.getSynTimeStamp();
            long serverConnDelay = tcpStream.getTcpConnAckTimeStamp() - tcpStream.getSackTimeStamp();
            int connAckFrameNumver = tcpStream.getTcpConnAckFrameNumber();

            Frame lastSackFrame = MapCache.getFrame(tcpStream.getLastSackFrameNumber());
            FrameJsonObject lastSackFramJson = lastSackFrame.toJsonObject();
            lastSackFramJson.setTcpClientConnectionDelay(String.valueOf(clientConnDelay));
            lastSackFrame.setFrameJson(lastSackFramJson);

            Frame connAckFrame = MapCache.getFrame(connAckFrameNumver);
            FrameJsonObject connAckFrameJson = connAckFrame.toJsonObject();
            connAckFrameJson.setTcpServerConnectionDelay(String.valueOf(serverConnDelay));
            connAckFrameJson.setTcpConnectionDelay(String.valueOf(clientConnDelay + serverConnDelay));
            connAckFrame.setFrameJson(connAckFrameJson);
        }
    }

    /**
     * 传输阶段，只计算建连成功的。
     *
     * @param tcpStream
     */
    private void onlySuccess(TcpStream tcpStream) {
        if (tcpStream.getTcpConnectionSuccess() == null || tcpStream.getTcpConnectionSuccess().equals(Boolean.FALSE)) {
            return;
        }

        List<Frame> frames = tcpStream.getFrames();
        for (Frame frame : frames) {
            FrameJsonObject json = frame.toJsonObject();
            onlySuccessRTT(tcpStream, json);
            onlySuccessRetrans(tcpStream, json);
            frame.setFrameJson(json);
        }
    }

    private void onlySuccessRTT(TcpStream tcpStream, FrameJsonObject json) {
        // 不计算三次握手的
        if (json.isTcpConnectionSyn() || json.isTcpConnectionSack()) {
            return;
        }
        if (json.getTcpAckFrameNumber() != null && !tcpStream.hasSackFrameNumber(json.getTcpAckFrameNumber())) {
            // 不计算tcp 长度 大于 0 的
            if (json.isTcpLenBt0()) {
                return;
            }

            Long rtt = json.getTcpAnalysisAckRtt();
            if (json.getSrcIp().equals(tcpStream.getServerIp())) {
                json.setClientIp(tcpStream.getClientIp());
                json.setServerIp(tcpStream.getServerIp());
                json.setTcpUpRTT(String.valueOf(rtt));
            } else if (json.getSrcIp().equals(tcpStream.getClientIp())) {
                json.setClientIp(tcpStream.getClientIp());
                json.setServerIp(tcpStream.getServerIp());
                json.setTcpDownRTT(String.valueOf(rtt));
            } else {
                LOGGER.error("Tcp stream: " + tcpStream.getTcpStreamNumber()
                        + ", Can't judge server ip or client ip, frame src_ip: " + json.getSrcIp());
            }
        }
    }

    private void onlySuccessRetrans(TcpStream tcpStream, FrameJsonObject json) {
        if (!json.isTcpLenBt0() || json.isTcpConnectionSyn() || json.isTcpConnectionSack() || json.isTcpConnectionRst()
                || json.isTcpConnectionFin() || json.isTcpKeepAlive() || json.isTcpDupAck()) {
            return;
        }

        json.setClientIp(tcpStream.getClientIp());
        json.setServerIp(tcpStream.getServerIp());

        if (tcpStream.getClientIp().equals(json.getSrcIp())) {
            json.setTcpUpPayload();
            if (json.isRetrans()) {
                json.setTcpUpRetrans();
            }
        } else if (tcpStream.getServerIp().equals(json.getSrcIp())) {
            json.setTcpDownPayload();
            if (json.isRetrans()) {
                json.setTcpDownRetrans();
            }
        } else {
            LOGGER.error("TcpStream=" + tcpStream.getTcpStreamNumber() + ", frameNumber=" + json.getFrameNumber() +
                    ", can't judge client ip or server ip.");
        }
    }
}
