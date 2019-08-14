package com.riil.ws.analysis.buf.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Adapter {
    private final Logger LOGGER = LoggerFactory.getLogger(Adapter.class);
    private JSONObject indexJson;
    private JSONObject json;
    private FrameBean frame;

    public FrameBean esJson2Bean(String esIndexJson, String frameJson) {
        frame = new FrameBean();
        indexJson = JSON.parseObject(esIndexJson);
        setIndex();

        json = JSON.parseObject(frameJson);
        setTimeStamp();
        setFrameNumber();
        setFrameLen();
        setFrameProto();
        setSrcIp();
        setDstIp();
        setTcpConnectionSyn();
        setTcpConnectionSack();
        setTcpConnectionRst();
        setTcpConnectionFin();
        setTcpStream();
        setTcpLen();
        setTcpSrcPort();
        setTcpDstPort();
        setTcpSeq();
        setTcpAck();
        setTcpAnalysisAcksFrame();
        setTcpAnalysisAckRtt();
        setTcpAnalysisDuplicateAck();
        setTcpFirstSegment();
        setTcpAnalysisRetransmission();
        setTcpAnalysisKeepAlive();
        setHttpRequest();
        setHttpResponse();
        setHttpRequestIn();
        setHttpResponseCode();

        return frame;
    }

    private void setIndex() {
        frame.setIndex((String) ((JSONObject) indexJson.get("index")).get("_index"));
    }

    private void setTimeStamp() {
        frame.setTimestamp(Long.parseLong((String) json.get("timestamp")));
    }

    private void setFrameNumber() {
        frame.getLayers().setFrameNumber(getIntegerLayerFirstBy(FrameConstant.FRAME_NUMBER));
    }

    private void setFrameLen() {
        frame.getLayers().setFrameLen(getIntegerLayerFirstBy(FrameConstant.FRAME_LEN));
    }


    /**
     * 由于ip_protoc 是 协议数组，取第一个协议为该frame的协议
     */
    @SuppressWarnings("unchecked")
    public void setFrameProto() {
        Object protos = getLayerBy(FrameConstant.IP_PROTO);
        if (protos != null) {
            Integer ipProto = Integer.valueOf(((List<String>) protos).get(0));
            frame.getLayers().setIpProto(ipProto);
            if (FrameConstant.TCP_PROTO_NUM.equals(ipProto)) {
                frame.getLayers().setTcp(true);
            }
        }
    }

    private void setTcpConnectionSyn() {
        frame.getLayers().setTcpConnectionSyn(getBooleanLayerBy(FrameConstant.TCP_CONNECTION_SYN));
    }

    private void setTcpConnectionSack() {
        frame.getLayers().setTcpConnectionSack(getBooleanLayerBy(FrameConstant.TCP_CONNECTION_SACK));
    }

    private void setTcpConnectionFin() {
        frame.getLayers().setTcpConnectionFin(getBooleanLayerBy(FrameConstant.TCP_CONNECTION_FIN));
    }

    private void setTcpConnectionRst() {
        frame.getLayers().setTcpConnectionRst(getBooleanLayerBy(FrameConstant.TCP_CONNECTION_RST));

    }

    private void setSrcIp() {
        frame.getLayers().setSrcIp(getLayerFirstBy(FrameConstant.IP_SRC));
    }

    private void setDstIp() {
        frame.getLayers().setDstIp(getLayerFirstBy(FrameConstant.IP_DST));
    }

    private void setTcpStream() {
        frame.getLayers().setTcpStream(getIntegerLayerFirstBy(FrameConstant.TCP_STREAM));
    }

    private void setTcpLen() {
        frame.getLayers().setTcpLen(getIntegerLayerFirstBy(FrameConstant.TCP_LEN));
    }

    private void setTcpSrcPort() {
        frame.getLayers().setTcpSrcPort(getIntegerLayerFirstBy(FrameConstant.TCP_SRCPORT));
    }

    private void setTcpDstPort() {
        frame.getLayers().setTcpDstPort(getIntegerLayerFirstBy(FrameConstant.TCP_DSTPORT));
    }

    private void setTcpSeq() {
        frame.getLayers().setTcpSeq(getLongLayerFirstBy(FrameConstant.TCP_SEQ));
    }

    private void setTcpAck() {
        frame.getLayers().setTcpAck(getLongLayerFirstBy(FrameConstant.TCP_ACK));
    }

    private void setTcpAnalysisAcksFrame() {
        frame.getLayers().setTcpAnalysisAcksFrame(getIntegerLayerFirstBy(FrameConstant.TCP_ANALYSIS_ACKS_FRAME));
    }

    private void setTcpAnalysisAckRtt() {
        frame.getLayers().setTcpAnalysisAckRtt(getFloatLayerFirstBy(FrameConstant.TCP_ANALYSIS_ACK_RTT));
    }

    private void setTcpAnalysisDuplicateAck() {
        frame.getLayers().setTcpAnalysisDuplicateAck(getBooleanLayerBy(FrameConstant.TCP_ANALYSIS_DUPLICATE_ACK));
    }

    private void setTcpFirstSegment() {
        frame.getLayers().setTcpFirstSegment(getIntegerLayerFirstBy(FrameConstant.TCP_FIRST_SEGMENT));
    }

    private void setTcpAnalysisRetransmission() {
        frame.getLayers().setTcpAnalysisRetransmission(getBooleanLayerBy(FrameConstant.TCP_ANALYSIS_RETRANSMISSION));
    }

    private void setTcpAnalysisKeepAlive() {
        frame.getLayers().setTcpAnalysisKeepAlive(getBooleanLayerBy(FrameConstant.TCP_ANALYSIS_KEEP_ALIVE));
    }

    private void setHttpRequest() {
        frame.getLayers().setHttpRequest(getBooleanLayerBy(FrameConstant.HTTP_REQUEST));
    }

    private void setHttpResponse() {
        frame.getLayers().setHttpResponse(getBooleanLayerBy(FrameConstant.HTTP_RESPONSE));
    }

    private void setHttpRequestIn() {
        frame.getLayers().setHttpRequestIn(getIntegerLayerFirstBy(FrameConstant.HTTP_REQUEST_IN));
    }

    private void setHttpResponseCode() {
        frame.getLayers().setHttpResponseCode(getIntegerLayerFirstBy(FrameConstant.HTTP_RESPONSE_CODE));
    }

    @SuppressWarnings("unchecked")
    private Integer getIntegerLayerFirstBy(String metric) {
        Object o = getLayerBy(metric);
        if (o != null) {
            return Integer.valueOf(((List<String>) o).get(0));
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Long getLongLayerFirstBy(String metric) {
        Object o = getLayerBy(metric);
        if (o != null) {
            return Long.valueOf(((List<String>) o).get(0));
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Float getFloatLayerFirstBy(String metric) {
        Object o = getLayerBy(metric);
        if (o != null) {
            return Float.valueOf(((List<String>) o).get(0));
        }

        return null;
    }

    private Boolean getBooleanLayerBy(String metric) {
        Object o = getLayerBy(metric);
        if (o != null) {
            return true;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private String getLayerFirstBy(String metric) {
        Object o = getLayerBy(metric);
        if (o != null) {
            return ((List<String>) o).get(0);
        }

        return null;
    }

    private Object getLayerBy(String metric) {
        return ((JSONObject) json.get(FrameConstant.LAYERS)).get(metric);
    }

    private void delLayerBy(String metric) {
        ((JSONObject) json.get(FrameConstant.LAYERS)).remove(metric);
    }
}
