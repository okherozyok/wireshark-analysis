package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.*;
import com.riil.ws.analysis.common.IpPortUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.riil.ws.analysis.buf.map.AnalyzerConstant.*;

/**
 * @author GlobalZ
 */
@Service
public class TcpAnalyzer {
    private final Logger LOGGER = LoggerFactory.getLogger(TcpAnalyzer.class);

    public void analysis(TcpStream tcpStream) throws Exception {

        List<FrameBean> frames = tcpStream.getFrames();
        for (FrameBean frame : frames) {
            markIpDirectionByFirst(frame, tcpStream);

            // 比如：icmp的type=11 code=0，意思是 Time to live exceeded in transit
            if (frame.containsIcmp()) {
                continue;
            }
            realTimeCreateConn(frame, tcpStream);
            http(frame, tcpStream);
        }

        endCreateConn(tcpStream);
        afterCreateConn(tcpStream);
    }

    private void markIpDirectionByFirst(FrameBean frame, TcpStream tcpStream) {
        if (!StringUtils.isEmpty(tcpStream.getClientIpByFirst())) {
            frame.setClientIpByFirst(tcpStream.getClientIpByFirst());
            frame.setServerIpByFirst(tcpStream.getServerIpByFirst());
            frame.setClientPortByFirst(tcpStream.getClientPortByFirst());
            frame.setServerPortByFirst(tcpStream.getServerPortByFirst());
            return;
        }

        if (frame.isTcpConnectionSyn()) {
            tcpStream.setClientIpByFirst(frame.getSrcIp());
            tcpStream.setServerIpByFirst(frame.getDstIp());
            tcpStream.setClientPortByFirst(frame.getTcpSrcPort());
            tcpStream.setServerPortByFirst(frame.getTcpDstPort());
        } else if (frame.isTcpConnectionSack()) {
            tcpStream.setClientIpByFirst(frame.getDstIp());
            tcpStream.setServerIpByFirst(frame.getSrcIp());
            tcpStream.setClientPortByFirst(frame.getTcpDstPort());
            tcpStream.setServerPortByFirst(frame.getTcpSrcPort());
        } else {
            // 没有syn 和 Sack时，与NPV一致，以第一条的srcIp作为客户端IP
            tcpStream.setClientIpByFirst(frame.getSrcIp());
            tcpStream.setServerIpByFirst(frame.getDstIp());
            tcpStream.setClientPortByFirst(frame.getTcpSrcPort());
            tcpStream.setServerPortByFirst(frame.getTcpDstPort());
        }
        frame.setClientIpByFirst(tcpStream.getClientIpByFirst());
        frame.setServerIpByFirst(tcpStream.getServerIpByFirst());
        frame.setClientPortByFirst(tcpStream.getClientPortByFirst());
        frame.setServerPortByFirst(tcpStream.getServerPortByFirst());
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
                tcpStream.setClientPort(json.getTcpSrcPort());
                tcpStream.setServerPort(json.getTcpDstPort());
            }

            // 可能发生了syn重传，清除建连成功的标志
            clearTcpConnSuccess(tcpStream);
        } else if (json.isTcpConnectionSack()) { // syn+ack包
            if (tcpStream.getClientIp() == null && tcpStream.getServerIp() == null) {
                // 丢失syn包的时候，纠正下客户端和服务端ip
                tcpStream.setClientIp(json.getDstIp());
                tcpStream.setServerIp(json.getSrcIp());
                tcpStream.setClientPort(json.getTcpSrcPort());
                tcpStream.setServerPort(json.getTcpDstPort());
            }
            tcpStream.addSackFrameNumber(json.getFrameNumber());
            tcpStream.setSackTimeStamp(json.getTimestamp());

            // 可能发生了syn+ack 重传，清除建连成功的标志
            clearTcpConnSuccess(tcpStream);
        } else if (json.isTcpConnectionRst()) { // rst包
            if (tcpStream.getTcpConnectionSuccess() == null) {
                if (!tcpStream.hasSyn()) {
                    LOGGER.debug("TcpStream: " + tcpStream.getTcpStreamNumber() + ", ifHasSyn=" + tcpStream.hasSyn()
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

    private void http(FrameBean frame, TcpStream tcpStream) {
        httpReqTransDelay(frame, tcpStream);
        httpRespAndRespTransDelay(frame, tcpStream);
        httpConcurrentReq(frame);
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

    private void httpConcurrentReq(FrameBean json) {
        if (!json.isHttpRequest()) {
            return;
        }

        final long second = 1000;
        long startTime = json.getTimestamp() / second * second;

        long ipPortKey = IpPortUtil.ipPortKey(json.getDstIp(), json.getTcpDstPort());
        Map<Long, Map<Long, ConcurrentReqBean>> concurrentReqCache = MapCache.getConcurrentReqCache();
        Map<Long, ConcurrentReqBean> concurrentReqBeanMap = concurrentReqCache.computeIfAbsent(ipPortKey, k -> new HashMap<>());
        ConcurrentReqBean concurrentReqBean = concurrentReqBeanMap.get(startTime);
        if (concurrentReqBean == null) {
            concurrentReqBean = new ConcurrentReqBean();
            concurrentReqBean.setIndex(json.getIndex().replace(PACKET_INDEX_PREFIX, HTTP_CONCURRENT_REQ_INDEX_PREFIX));
            concurrentReqBean.setServerIp(json.getDstIp());
            concurrentReqBean.setTcpDstPort(json.getTcpDstPort());
            concurrentReqBean.setTimestamp(startTime);
            concurrentReqBeanMap.put(startTime, concurrentReqBean);
        }
        concurrentReqBean.addReqCount();
        concurrentReqBean.addFrameNumber(json.getFrameNumber());
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
            LOGGER.debug("TcpStream=" + tcpStream.getTcpStreamNumber() + ", frameNumber=" + json.getFrameNumber() + ", httpRequestIn is null.");
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
                LOGGER.debug("TcpStream: " + tcpStream.getTcpStreamNumber() + ", ifHasSyn=" + tcpStream.hasSyn()
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
    private void afterCreateConn(TcpStream tcpStream) {
        List<FrameBean> frames = tcpStream.getFrames();
        for (FrameBean frame : frames) {
            onlineUser(tcpStream, frame);

            onlySuccessRTT(tcpStream, frame);
            onlySuccessRetrans(tcpStream, frame);
            onlySuccessDisconnectionStart(tcpStream, frame);
        }

        onlySuccessDisconnectionEnd(tcpStream);
        onlySuccessConcurrentConn(tcpStream);
    }

    private void onlySuccessRTT(TcpStream tcpStream, FrameBean frame) {
        if (!ifTcpConnectionSuccess(tcpStream)) {
            return;
        }

        // 不计算三次握手的
        if (frame.isTcpConnectionSyn() || frame.isTcpConnectionSack()) {
            return;
        }
        if (frame.getTcpAckFrameNumber() != null && !tcpStream.hasSackFrameNumber(frame.getTcpAckFrameNumber())) {
            // 不计算tcp 长度 大于 0 的
            if (frame.isTcpLenBt0()) {
                return;
            }

            Long rtt = frame.getTcpAnalysisAckRtt();
            if (frame.getSrcIp().equals(tcpStream.getServerIp())) {
                frame.setClientIp(tcpStream.getClientIp());
                frame.setServerIp(tcpStream.getServerIp());
                frame.setTcpUpRTT(rtt);
            } else if (frame.getSrcIp().equals(tcpStream.getClientIp())) {
                frame.setClientIp(tcpStream.getClientIp());
                frame.setServerIp(tcpStream.getServerIp());
                frame.setTcpDownRTT(rtt);
            } else {
                LOGGER.error("Tcp stream: " + tcpStream.getTcpStreamNumber()
                        + ", Can't judge server ip or client ip, frame src_ip: " + frame.getSrcIp());
            }
        }
    }

    private void onlySuccessRetrans(TcpStream tcpStream, FrameBean frame) {
        if (!ifTcpConnectionSuccess(tcpStream)) {
            return;
        }

        if (!frame.isTcpLenBt0() || frame.isTcpConnectionSyn() || frame.isTcpConnectionSack() || frame.isTcpConnectionRst()
                || frame.isTcpConnectionFin() || frame.isTcpKeepAlive() || frame.isTcpDupAck()) {
            return;
        }

        frame.setClientIp(tcpStream.getClientIp());
        frame.setServerIp(tcpStream.getServerIp());

        if (tcpStream.getClientIp().equals(frame.getSrcIp())) {
            frame.setTcpUpPayload();
            if (frame.isRetrans()) {
                frame.setTcpUpRetrans();
            }
        } else if (tcpStream.getServerIp().equals(frame.getSrcIp())) {
            frame.setTcpDownPayload();
            if (frame.isRetrans()) {
                frame.setTcpDownRetrans();
            }
        } else {
            LOGGER.error("TcpStream=" + tcpStream.getTcpStreamNumber() + ", frameNumber=" + frame.getFrameNumber() +
                    ", can't judge client ip or server ip.");
        }
    }

    private void onlySuccessConcurrentConn(TcpStream tcpStream) {
        if (!ifTcpConnectionSuccess(tcpStream)) {
            return;
        }

        final long second = 1000;

        List<FrameBean> frames = tcpStream.getFrames();
        FrameBean firstFrame = frames.get(0);
        String firstFrameIndex = firstFrame.getIndex();
        FrameBean lastFrame = frames.get(frames.size() - 1);

        long startTime = firstFrame.getTimestamp() / second * second;
        long endTime = lastFrame.getTimestamp() / second * second;

        long ipPortKey = IpPortUtil.ipPortKey(tcpStream.getServerIp(), tcpStream.getServerPort());
        Map<Long, Map<Long, ConcurrentConnBean>> concurrentConnCache = MapCache.getConcurrentConnCache();
        Map<Long, ConcurrentConnBean> concurrentConnBeanMap = concurrentConnCache.computeIfAbsent(ipPortKey, k -> new HashMap<>());
        while (startTime <= endTime) {
            ConcurrentConnBean concurrentConnBean = concurrentConnBeanMap.get(startTime);
            if (concurrentConnBean == null) {
                concurrentConnBean = new ConcurrentConnBean();
                concurrentConnBean.setIndex(firstFrameIndex.replace(PACKET_INDEX_PREFIX, TCP_CONCURRENT_CONN_INDEX_PREFIX));
                concurrentConnBean.setServerIp(tcpStream.getServerIp());
                concurrentConnBean.setTcpDstPort(tcpStream.getServerPort());
                concurrentConnBean.setTimestamp(startTime);
                concurrentConnBeanMap.put(startTime, concurrentConnBean);
            }
            concurrentConnBean.addConnectCount();
            concurrentConnBean.addTcpStream(tcpStream.getTcpStreamNumber());

            startTime += second;
        }
    }

    /**
     * 仅建连成功时，记录拆连
     *
     * @param tcpStream
     * @param frame
     */
    private void onlySuccessDisconnectionStart(TcpStream tcpStream, FrameBean frame) {
        if (!ifTcpConnectionSuccess(tcpStream)) {
            return;
        }

        if (frame.isTcpConnectionFin()) {
            if (tcpStream.getClientIp().equals(frame.getSrcIp())) {
                if (tcpStream.getClientFinFrame() == null) {
                    tcpStream.setClientFinFrame(frame.getFrameNumber());
                }
            } else {
                if (tcpStream.getServerFinFrame() == null) {
                    tcpStream.setServerFinFrame(frame.getFrameNumber());
                }
            }
        } else if (frame.isTcpConnectionRst()) {
            if (tcpStream.getClientIp().equals(frame.getSrcIp())) {
                if (tcpStream.getClientRstFrame() == null) {
                    tcpStream.setClientRstFrame(frame.getFrameNumber());
                }
            } else {
                if (tcpStream.getServerRstFrame() == null) {
                    tcpStream.setServerRstFrame(frame.getFrameNumber());
                }
            }
        }
    }

    /**
     * 拆连结果统计
     *
     * @param tcpStream
     */
    private void onlySuccessDisconnectionEnd(TcpStream tcpStream) {
        if (!ifTcpConnectionSuccess(tcpStream)) {
            return;
        }

        // FIXME 猎豹按照syslog的开始时间统计的拆连状态，所以临时把标记标在第一个frame上
        FrameBean frame = null;
        FrameBean firstFrame = tcpStream.getFrames().get(0);
        if (tcpStream.getClientFinFrame() != null && tcpStream.getClientRstFrame() != null) {
            if (tcpStream.getClientRstFrame() > tcpStream.getClientFinFrame()) {
                frame = MapCache.getFrame(tcpStream.getClientRstFrame());
            } else {
                LOGGER.debug("TcpStream=" + tcpStream.getTcpStreamNumber() + " clientRstFrame="
                        + tcpStream.getClientRstFrame() + " < clientFinFrame=" + tcpStream.getClientFinFrame());
                frame = MapCache.getFrame(tcpStream.getClientFinFrame());
            }
            //frame.setTcpClientDisconnectionFinRst();
            firstFrame.setTcpClientDisconnectionFinRst();
        } else if (tcpStream.getServerFinFrame() != null && tcpStream.getServerRstFrame() != null) {
            if (tcpStream.getServerRstFrame() > tcpStream.getServerFinFrame()) {
                frame = MapCache.getFrame(tcpStream.getServerRstFrame());
            } else {
                LOGGER.debug("TcpStream=" + tcpStream.getTcpStreamNumber() + " serverRstFrame="
                        + tcpStream.getServerRstFrame() + " < serverFinFrame=" + tcpStream.getServerFinFrame());
                frame = MapCache.getFrame(tcpStream.getServerFinFrame());
            }
            //frame.setTcpServerDisconnectionFinRst();
            firstFrame.setTcpServerDisconnectionFinRst();
        } else if (tcpStream.getClientRstFrame() != null) {
            frame = MapCache.getFrame(tcpStream.getClientRstFrame());
            //frame.setTcpClientDisconnectionRst();
            firstFrame.setTcpClientDisconnectionRst();
        } else if (tcpStream.getServerRstFrame() != null) {
            frame = MapCache.getFrame(tcpStream.getServerRstFrame());
            //frame.setTcpServerDisconnectionRst();
            firstFrame.setTcpServerDisconnectionRst();
        } else if (tcpStream.getClientFinFrame() != null && tcpStream.getServerFinFrame() != null) {
            if (tcpStream.getClientFinFrame() > tcpStream.getServerFinFrame()) {
                frame = MapCache.getFrame(tcpStream.getClientFinFrame());
            } else {
                frame = MapCache.getFrame(tcpStream.getServerFinFrame());
            }
            //frame.setTcpDisConnectionNormal();
            firstFrame.setTcpDisConnectionNormal();
        } else if (tcpStream.getClientFinFrame() != null) {
            frame = MapCache.getFrame(tcpStream.getClientFinFrame());
            //frame.setTcpClientDisconnectionFinNoResp();
            firstFrame.setTcpClientDisconnectionFinNoResp();
        } else if (tcpStream.getServerFinFrame() != null) {
            frame = MapCache.getFrame(tcpStream.getServerFinFrame());
            //frame.setTcpServerDisconnectionFinNoResp();
            firstFrame.setTcpServerDisconnectionFinNoResp();
        }
        //if (frame != null) {
        if (firstFrame != null) {
            //frame.setClientIp(tcpStream.getClientIp());
            firstFrame.setClientIp(tcpStream.getClientIp());
            //frame.setServerIp(tcpStream.getServerIp());
            firstFrame.setServerIp(tcpStream.getServerIp());
            //frame.setClientPort(tcpStream.getClientPort());
            firstFrame.setClientPort(tcpStream.getClientPort());
            //frame.setServerPort(tcpStream.getDstPort());
            firstFrame.setServerPort(tcpStream.getServerPort());
        } else {
            LOGGER.debug("tcpStream=" + tcpStream.getTcpStreamNumber() + " has no disconnection.");
        }
    }

    /**
     * 在tcpStream的第一条报文和每新的1分钟报文上，设置在线用户
     *
     * @param tcpStream
     */
    private void onlineUser(TcpStream tcpStream, FrameBean frame) {
        final int minute = 60000;
        long wholeMinute = frame.getTimestamp() / minute * minute;
        if (tcpStream.getOnlineUserTimeStamp() == null) {
            tcpStream.setOnlineUserTimeStamp(wholeMinute);
            frame.setOnlineUser(tcpStream.getClientIpByFirst());
        } else {
            if (wholeMinute > tcpStream.getOnlineUserTimeStamp()) {
                tcpStream.setOnlineUserTimeStamp(wholeMinute);
                // 在线用户的 客户端、服务端要和第一条标记的保持一致
                frame.setOnlineUser(tcpStream.getClientIpByFirst());
            }
        }
    }

    private boolean ifTcpConnectionSuccess(TcpStream tcpStream) {
        return Boolean.TRUE.equals(tcpStream.getTcpConnectionSuccess());
    }
}
