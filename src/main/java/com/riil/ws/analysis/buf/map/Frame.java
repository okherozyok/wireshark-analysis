package com.riil.ws.analysis.buf.map;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author GlobalZ
 */
public class Frame {
    private String esIndexJson;
    private String frameJson;

    public String getEsIndexJson() {
        return esIndexJson;
    }

    public void setEsIndexJson(String esIndexJson) {
        this.esIndexJson = esIndexJson;
    }

    public String getFrameJson() {
        return frameJson;
    }

    public void setFrameJson(String frameJson) {
        this.frameJson = frameJson;
    }

    /**
     * 由于ip_protoc 是 协议数组，取第一个协议为该frame的协议
     */
    @SuppressWarnings("unchecked")
    public void setFrameProto() {
        JSONObject parseObject = JSON.parseObject(getFrameJson());
        JSONObject layers = (JSONObject) parseObject.get(FrameConstant.LAYERS);
        Object protos = layers.get(FrameConstant.IP_PROTO);
        if (protos != null) {
            if (FrameConstant.TCP_PROTO_NUM.equals(Integer.valueOf(((List<String>) protos).get(0)))) {
                layers.put(FrameConstant.TCP, FrameConstant.SIGN_PLACEHOLDER);
                setFrameJson(JSON.toJSONString(parseObject));
            }
        }
    }

    public Integer getFrameNumber() {
        return Integer.valueOf(getLayerFirstBy(FrameConstant.FRAME_NUMBER));
    }

    public Integer getTcpAckFrameNumber() {
        return getIntegerLayerFirstBy(FrameConstant.TCP_ANALYSIS_ACKS_FRAME);
    }

    public Long getTcpAnalysisAckRtt() {
        String layerFirstBy = getLayerFirstBy(FrameConstant.TCP_ANALYSIS_ACK_RTT);
        BigDecimal bd = new BigDecimal(Double.valueOf(layerFirstBy));
        return bd.setScale(3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1000L)).longValue();
    }

    public Long getTcpLen() {
        return getLongLayerFirstBy(FrameConstant.TCP_LEN);
    }

    public Integer getTcpStreamNumber() {
        return getIntegerLayerFirstBy(FrameConstant.TCP_STREAM);
    }

    public Long getTimeStamp() {
        JSONObject parseObject = JSON.parseObject(getFrameJson());
        return Long.valueOf((String) parseObject.get(FrameConstant.TIMESTAMP));
    }

    public boolean isTcp() {
        Object tcpConnectionSyn = getLayerBy(FrameConstant.TCP);
        if (tcpConnectionSyn != null) {
            return true;
        }

        return false;
    }

    public boolean isTcpConnectionSyn() {
        Object tcpConnectionSyn = getLayerBy(FrameConstant.TCP_CONNECTION_SYN);
        if (tcpConnectionSyn != null) {
            return true;
        }

        return false;
    }

    public boolean isTcpConnectionSack() {
        Object tcpConnectionSack = getLayerBy(FrameConstant.TCP_CONNECTION_SACK);
        if (tcpConnectionSack != null) {
            return true;
        }

        return false;
    }

    public boolean isTcpConnectionRst() {
        Object tcpConnectionRst = getLayerBy(FrameConstant.TCP_CONNECTION_RST);
        if (tcpConnectionRst != null) {
            return true;
        }

        return false;
    }

    public boolean isHttpRequest() {
        Object httpRequest = getLayerBy(FrameConstant.HTTP_REQUEST);
        if (httpRequest != null) {
            return true;
        }

        return false;
    }

    public boolean isHttpResponse() {
        Object httpResponse = getLayerBy(FrameConstant.HTTP_RESPONSE);
        if (httpResponse != null) {
            return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public Integer getFirstTcpSegmentIfHas() {
        List<String> tcpSegments = (List<String>) getLayerBy(FrameConstant.TCP_SEGMENT);
        if (tcpSegments != null) {
            return Integer.valueOf(tcpSegments.get(0));
        }

        return null;
    }


    public String getSrcIp() {
        return getLayerFirstBy(FrameConstant.IP_SRC);
    }

    public String getDstIp() {
        return getLayerFirstBy(FrameConstant.IP_DST);
    }

    public Long getTcpSeq() {
        return Long.valueOf(getLayerFirstBy(FrameConstant.TCP_SEQ));
    }

    public Long getTcpAck() {
        return Long.valueOf(getLayerFirstBy(FrameConstant.TCP_ACK));
    }

    @SuppressWarnings("unchecked")
    public Integer getHttpRequestIn() {
        Object o = getLayerBy(FrameConstant.HTTP_REQUEST_IN);
        if (o != null) {
            return Integer.valueOf(((List<String>) o).get(0));
        }
        return null;
    }

    public void setClientIp(String clientIp) {
        setLayerBy(FrameConstant.CLIENT_IP, clientIp);
    }

    public void delClientIp() {
        delLayerBy(FrameConstant.CLIENT_IP);
    }

    public void setServerIp(String serverIp) {
        setLayerBy(FrameConstant.SERVER_IP, serverIp);
    }

    public void delServerIp() {
        delLayerBy(FrameConstant.SERVER_IP);
    }

    public void setTcpClientConnectionRst() {
        setLayerBy(FrameConstant.TCP_CLIENT_CONNECTION_RST, FrameConstant.SIGN_PLACEHOLDER);
    }

    public void setTcpServerConnectionRst() {
        setLayerBy(FrameConstant.TCP_SERVER_CONNECTION_RST, FrameConstant.SIGN_PLACEHOLDER);
    }

    public void setTcpClientConnectionNoResp() {
        setLayerBy(FrameConstant.TCP_CLIENT_CONNECTION_NO_RESP, FrameConstant.SIGN_PLACEHOLDER);
    }

    public void setTcpServerConnectionNoResp() {
        setLayerBy(FrameConstant.TCP_SERVER_CONNECTION_NO_RESP, FrameConstant.SIGN_PLACEHOLDER);
    }

    public void setTcpConnectionSuccess() {
        setLayerBy(FrameConstant.TCP_CONNECTION_SUCCESS, FrameConstant.SIGN_PLACEHOLDER);
    }

    public void delTcpConnectionSuccess() {
        delLayerBy(FrameConstant.TCP_CONNECTION_SUCCESS);
    }

    public void setTcpClientConnectionDelay(String delay) {
        setLayerBy(FrameConstant.TCP_CLIENT_CONNECTION_DELAY, delay);
    }

    public void setTcpServerConnectionDelay(String delay) {
        setLayerBy(FrameConstant.TCP_SERVER_CONNECTION_DELAY, delay);
    }

    public void setTcpConnectionDelay(String delay) {
        setLayerBy(FrameConstant.TCP_CONNECTION_DELAY, delay);
    }

    public void setTcpUpRTT(String rtt) {
        setLayerBy(FrameConstant.TCP_UP_RTT, rtt);
    }

    public void setTcpDownRTT(String rtt) {
        setLayerBy(FrameConstant.TCP_DOWN_RTT, rtt);
    }

    public void setHttpReqTransDelay(String delay) {
        setLayerBy(FrameConstant.HTTP_REQ_TRANS_DELAY, delay);
    }

    public void setHttpRespDelay(String delay) {
        setLayerBy(FrameConstant.HTTP_RESP_DELAY, delay);
    }

    public void setHttpRespTransDelay(String delay) {
        setLayerBy(FrameConstant.HTTP_RESP_TRANS_DELAY, delay);
    }

    @SuppressWarnings("unchecked")
    private Long getLongLayerFirstBy(String metric) {
        Object tcpStream = getLayerBy(metric);
        if (tcpStream != null) {
            return Long.valueOf(((List<String>) tcpStream).get(0));
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Integer getIntegerLayerFirstBy(String metric) {
        Object tcpStream = getLayerBy(metric);
        if (tcpStream != null) {
            return Integer.valueOf(((List<String>) tcpStream).get(0));
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private String getLayerFirstBy(String metric) {
        return ((List<String>) getLayerBy(metric)).get(0);
    }

    private Object getLayerBy(String metric) {
        JSONObject parseObject = JSON.parseObject(getFrameJson());
        return ((JSONObject) parseObject.get(FrameConstant.LAYERS)).get(metric);
    }

    private void setLayerBy(String metric, String value) {
        JSONObject parseObject = JSON.parseObject(getFrameJson());
        ((JSONObject) parseObject.get(FrameConstant.LAYERS)).put(metric, value);
        setFrameJson(JSON.toJSONString(parseObject));
    }

    private void delLayerBy(String metric) {
        JSONObject parseObject = JSON.parseObject(getFrameJson());
        ((JSONObject) parseObject.get(FrameConstant.LAYERS)).remove(metric);
        setFrameJson(JSON.toJSONString(parseObject));
    }
}
