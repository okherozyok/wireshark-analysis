package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.ConcurrentConnBean;
import com.riil.ws.analysis.buf.map.FrameBean;
import com.riil.ws.analysis.buf.map.FrameConstant;
import com.riil.ws.analysis.buf.map.MapCache;
import com.riil.ws.analysis.common.IpPortUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.riil.ws.analysis.buf.map.AnalyzerConstant.PACKET_INDEX_PREFIX;
import static com.riil.ws.analysis.buf.map.AnalyzerConstant.TCP_CONCURRENT_CONN_INDEX_PREFIX;

/**
 * @author GlobalZ
 */
@Service
public class TcpAnalyzer {
    private final Logger LOGGER = LoggerFactory.getLogger(TcpAnalyzer.class);

    public void analysis(TcpStream tcpStream) throws Exception {

        List<FrameBean> frames = tcpStream.getFrames();
        for (FrameBean frame : frames) {
            realTimeCreateConn(frame, tcpStream);
            httpDelay(frame, tcpStream);
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
     */
    private void realTimeCreateConn(FrameBean json, TcpStream tcpStream) {
        if (json.isTcpConnectionSyn()) { // syn包
            tcpStream.setHasSyn(true);

            // 只记录第一个syn包的时间
            if (tcpStream.getSynTimeStamp() == null) {
                tcpStream.setSynTimeStamp(json.getTimestamp());
                tcpStream.setClientIp(json.getSrcIp());
                tcpStream.setServerIp(json.getDstIp());
                tcpStream.setDstPort(json.getTcpDstPort());
            }

            // 可能发生了syn重传，清除建连成功的标志
            clearTcpConnSuccess(tcpStream);
        } else if (json.isTcpConnectionSack()) { // syn+ack包
            if (tcpStream.getClientIp() == null && tcpStream.getServerIp() == null) {
                // 丢失syn包的时候，纠正下客户端和服务端ip
                tcpStream.setClientIp(json.getDstIp());
                tcpStream.setServerIp(json.getSrcIp());
                tcpStream.setDstPort(json.getTcpDstPort());
            }
            tcpStream.addSackFrameNumber(json.getFrameNumber());
            tcpStream.setSackTimeStamp(json.getTimestamp());

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

    private void httpDelay(FrameBean frame, TcpStream tcpStream) {
        httpReqTransDelay(frame, tcpStream);
        httpRespAndRespTransDelay(frame, tcpStream);
    }

    private void markTcpConnSuccess(TcpStream tcpStream, FrameBean frame) {
        tcpStream.setTcpConnAckTimeStamp(frame.getTimestamp());
        tcpStream.setTcpConnAckFrameNumber(frame.getFrameNumber());

        // 建连成功，如果发生syn或者syn+ack的重传，这个标志会被清除
        frame.setTcpConnectionSuccess();
        frame.setClientIp(tcpStream.getClientIp());
        frame.setServerIp(tcpStream.getServerIp());
        tcpStream.setTcpConnectionSuccess(Boolean.TRUE);
    }

    /**
     * 遇到syn和syn+ack时被调用，如果已经是建连成功的，清除建连成功状态。
     */
    private void clearTcpConnSuccess(TcpStream tcpStream) {
        if (Boolean.TRUE.equals(tcpStream.getTcpConnectionSuccess())) {
            FrameBean frame = MapCache.getFrame(tcpStream.getTcpConnAckFrameNumber());
            frame.delClientIp();
            frame.delServerIp();
            frame.delTcpConnectionSuccess();
            tcpStream.setTcpConnectionSuccess(null);
            tcpStream.setTcpConnAckFrameNumber(null);
            tcpStream.setTcpConnAckTimeStamp(null);
        }
    }

    private void httpReqTransDelay(FrameBean json, TcpStream tcpStream) {
        if (!json.isHttpRequest()) {
            return;
        }

        // 根据http_request，可以判断一次客户端和服务端
        json.setClientIp(json.getSrcIp());
        json.setServerIp(json.getDstIp());

        Integer firstSegment = json.getFirstTcpSegmentIfHas();
        if (firstSegment == null) {
            json.setHttpReqTransDelay(Long.valueOf(FrameConstant.ZERO_STRING));
        } else {
            Long delay = json.getTimestamp()
                    - MapCache.getFrame(firstSegment).getTimestamp();
            json.setHttpReqTransDelay(delay);
        }
    }

    private void httpRespAndRespTransDelay(FrameBean json, TcpStream tcpStream) {
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
                Long delay = json.getTimestamp()
                        - MapCache.getFrame(httpRequestIn).getTimestamp();
                json.setHttpRespDelay(delay);
            }
            json.setHttpRespTransDelay(Long.valueOf(FrameConstant.ZERO_STRING));
            // 根据http_response，可以判断一次客户端和服务端
            json.setClientIp(json.getDstIp());
            json.setServerIp(json.getSrcIp());
        } else {
            if (httpRequestIn != null) {
                FrameBean firstSegmentFrame = MapCache.getFrame(firstSegment);
                Long respDelay = firstSegmentFrame.getTimestamp()
                        - MapCache.getFrame(httpRequestIn).getTimestamp();
                firstSegmentFrame.setHttpRespDelay(respDelay);
                // 根据http_response，可以判断一次客户端和服务端
                firstSegmentFrame.setClientIp(json.getDstIp());
                firstSegmentFrame.setServerIp(json.getSrcIp());
            }
            Long respTransDelay = json.getTimestamp()
                    - MapCache.getFrame(firstSegment).getTimestamp();
            json.setHttpRespTransDelay(respTransDelay);
            // 根据http_response，可以判断一次客户端和服务端
            json.setClientIp(json.getDstIp());
            json.setServerIp(json.getSrcIp());
        }
    }

    /**
     * 结束时建连状态分析，判断建连无响应的情况，判断一个tcpStream是否有syn包。
     * <p>
     * 结束时建连时延分析，判断出客户端建连时延、服务端建连时延、总建连时延
     */
    private void endCreateConn(TcpStream tcpStream) {

        if (tcpStream.getTcpConnectionSuccess() == null) {
            if (!tcpStream.hasSyn()) {
                LOGGER.warn("TcpStream: " + tcpStream.getTcpStreamNumber() + ", ifHasSyn=" + tcpStream.hasSyn()
                        + ", can't judge client or server no response.");
            } else {
                List<FrameBean> frames = tcpStream.getFrames();
                FrameBean lastFrame = frames.get(frames.size() - 1);
                lastFrame.setClientIp(tcpStream.getClientIp());
                lastFrame.setServerIp(tcpStream.getServerIp());
                if (tcpStream.getSynTimeStamp() != null && tcpStream.getSackTimeStamp() != null) {
                    lastFrame.setTcpClientConnectionNoResp();
                } else if (tcpStream.getSynTimeStamp() != null) {
                    lastFrame.setTcpServerConnectionNoResp();
                }
            }
        } else if (Boolean.TRUE.equals(tcpStream.getTcpConnectionSuccess())) { // 建连成功时，计算建连时延
            long clientConnDelay = tcpStream.getSackTimeStamp() - tcpStream.getSynTimeStamp();
            long serverConnDelay = tcpStream.getTcpConnAckTimeStamp() - tcpStream.getSackTimeStamp();
            int connAckFrameNumver = tcpStream.getTcpConnAckFrameNumber();

            FrameBean lastSackFrame = MapCache.getFrame(tcpStream.getLastSackFrameNumber());
            lastSackFrame.setTcpClientConnectionDelay(clientConnDelay);

            FrameBean connAckFrame = MapCache.getFrame(connAckFrameNumver);
            connAckFrame.setTcpServerConnectionDelay(serverConnDelay);
            connAckFrame.setTcpConnectionDelay(clientConnDelay + serverConnDelay);
        }
    }

    /**
     * 传输阶段，只计算建连成功的。
     */
    private void onlySuccess(TcpStream tcpStream) {
        if (tcpStream.getTcpConnectionSuccess() == null || tcpStream.getTcpConnectionSuccess().equals(Boolean.FALSE)) {
            return;
        }

        List<FrameBean> frames = tcpStream.getFrames();
        for (FrameBean frame : frames) {
            onlySuccessRTT(tcpStream, frame);
            onlySuccessRetrans(tcpStream, frame);
        }

        onlySuccessConcurrentConn(tcpStream);
    }

    private void onlySuccessRTT(TcpStream tcpStream, FrameBean json) {
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
                json.setTcpUpRTT(rtt);
            } else if (json.getSrcIp().equals(tcpStream.getClientIp())) {
                json.setClientIp(tcpStream.getClientIp());
                json.setServerIp(tcpStream.getServerIp());
                json.setTcpDownRTT(rtt);
            } else {
                LOGGER.error("Tcp stream: " + tcpStream.getTcpStreamNumber()
                        + ", Can't judge server ip or client ip, frame src_ip: " + json.getSrcIp());
            }
        }
    }

    private void onlySuccessRetrans(TcpStream tcpStream, FrameBean json) {
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

    private void onlySuccessConcurrentConn(TcpStream tcpStream) {
        final long second = 1000;

        List<FrameBean> frames = tcpStream.getFrames();
        FrameBean firstFrame = frames.get(0);
        String firstFrameIndex = firstFrame.getIndex();
        FrameBean lastFrame = frames.get(frames.size() - 1);

        long startTime = firstFrame.getTimestamp() / second * second;
        long endTime = lastFrame.getTimestamp() / second * second;

        long ipPortKey = IpPortUtil.ipPortKey(tcpStream.getServerIp(), tcpStream.getDstPort());
        Map<Long, Map<Long, ConcurrentConnBean>> concurrentConnCache = MapCache.getConcurrentConnCache();
        Map<Long, ConcurrentConnBean> concurrentConnBeanMap = concurrentConnCache.get(ipPortKey);
        if (concurrentConnBeanMap == null) {
            concurrentConnBeanMap = new HashMap<>();
            concurrentConnCache.put(ipPortKey, concurrentConnBeanMap);
        }
        while (startTime <= endTime) {
            ConcurrentConnBean concurrentConnBean = concurrentConnBeanMap.get(startTime);
            if (concurrentConnBean == null) {
                concurrentConnBean = new ConcurrentConnBean();
                concurrentConnBean.setIndex(firstFrameIndex.replace(PACKET_INDEX_PREFIX, TCP_CONCURRENT_CONN_INDEX_PREFIX));
                concurrentConnBean.setServerIp(tcpStream.getServerIp());
                concurrentConnBean.setTcpDstPort(tcpStream.getDstPort());
                concurrentConnBean.setTimestamp(startTime);
                concurrentConnBeanMap.put(startTime, concurrentConnBean);
            }
            concurrentConnBean.addConnectCount();
            concurrentConnBean.addTcpStream(tcpStream.getTcpStreamNumber());

            startTime += second;
        }
    }
}