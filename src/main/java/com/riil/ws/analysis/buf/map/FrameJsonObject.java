package com.riil.ws.analysis.buf.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class FrameJsonObject {
    private JSONObject json;

    FrameJsonObject(Map<String, Object> json) {
        this.json = (JSONObject) json;
    }

    public String toJsonString() {
        return json.toJSONString();
    }

    public boolean isTcp() {
        Object tcpConnectionSyn = getLayerBy(FrameConstant.TCP);
        return tcpConnectionSyn != null;
    }

    public Boolean isTcpLenBt0() {
        String layerFirstBy = getLayerFirstBy(FrameConstant.TCP_LEN);
        if (layerFirstBy == null) {
            return null;
        }

        return Integer.parseInt(layerFirstBy) > 0;
    }

    public boolean isTcpConnectionSyn() {
        Object tcpConnectionSyn = getLayerBy(FrameConstant.TCP_CONNECTION_SYN);
        return tcpConnectionSyn != null;
    }

    public boolean isTcpConnectionSack() {
        Object tcpConnectionSack = getLayerBy(FrameConstant.TCP_CONNECTION_SACK);
        return tcpConnectionSack != null;
    }

    public boolean isTcpConnectionRst() {
        Object tcpConnectionRst = getLayerBy(FrameConstant.TCP_CONNECTION_RST);
        return tcpConnectionRst != null;
    }

    public boolean isTcpConnectionFin() {
        Object tcpConnectionRst = getLayerBy(FrameConstant.TCP_CONNECTION_FIN);
        return tcpConnectionRst != null;
    }

    public boolean isTcpKeepAlive() {
        Object keepAlive = getLayerBy(FrameConstant.TCP_ANALYSIS_KEEP_ALIVE);
        return keepAlive != null;
    }

    public boolean isTcpDupAck() {
        Object dupAck = getLayerBy(FrameConstant.TCP_ANALYSIS_DUPLICATE_ACK);
        return dupAck != null;
    }

    public boolean isRetrans() {
        Object retrans = getLayerBy(FrameConstant.TCP_ANALYSIS_RETRANSMISSION);
        if (retrans != null) {
            return true;
        }
        retrans = getLayerBy(FrameConstant.TCP_ANALYSIS_SPURIOUS_RETRANSMISSION);
        if (retrans != null) {
            return true;
        }
        retrans = getLayerBy(FrameConstant.TCP_ANALYSIS_FAST_RETRANSMISSION);
        if (retrans != null) {
            return true;
        }

        return false;
    }

    public boolean isHttpRequest() {
        Object httpRequest = getLayerBy(FrameConstant.HTTP_REQUEST);
        return httpRequest != null;
    }

    public boolean isHttpResponse() {
        Object httpResponse = getLayerBy(FrameConstant.HTTP_RESPONSE);
        return httpResponse != null;
    }

    public Long getTimeStamp() {
        return Long.valueOf((String) json.get(FrameConstant.TIMESTAMP));
    }

    public Integer getFrameNumber() {
        return getIntegerLayerFirstBy(FrameConstant.FRAME_NUMBER);
    }

    public Integer getTcpStreamNumber() {
        return getIntegerLayerFirstBy(FrameConstant.TCP_STREAM);
    }

    public Integer getTcpAckFrameNumber() {
        return getIntegerLayerFirstBy(FrameConstant.TCP_ANALYSIS_ACKS_FRAME);
    }

    public Long getTcpAnalysisAckRtt() {
        String layerFirstBy = getLayerFirstBy(FrameConstant.TCP_ANALYSIS_ACK_RTT);
        BigDecimal bd = new BigDecimal(Double.parseDouble(layerFirstBy));
        return bd.setScale(3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1000L)).longValue();
    }

    public String getSrcIp() {
        return getLayerFirstBy(FrameConstant.IP_SRC);
    }

    public String getDstIp() {
        return getLayerFirstBy(FrameConstant.IP_DST);
    }

    @SuppressWarnings("unchecked")
    public Integer getFirstTcpSegmentIfHas() {
        List<String> tcpSegments = (List<String>) getLayerBy(FrameConstant.TCP_SEGMENT);
        if (tcpSegments != null) {
            return Integer.valueOf(tcpSegments.get(0));
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Integer getHttpRequestIn() {
        Object o = getLayerBy(FrameConstant.HTTP_REQUEST_IN);
        if (o != null) {
            return Integer.valueOf(((List<String>) o).get(0));
        }
        return null;
    }

    /**
     * 由于ip_protoc 是 协议数组，取第一个协议为该frame的协议
     */
    @SuppressWarnings("unchecked")
    public void setFrameProto() {
        JSONObject layers = (JSONObject) json.get(FrameConstant.LAYERS);
        Object protos = layers.get(FrameConstant.IP_PROTO);
        if (protos != null) {
            if (FrameConstant.TCP_PROTO_NUM.equals(Integer.valueOf(((List<String>) protos).get(0)))) {
                layers.put(FrameConstant.TCP, FrameConstant.SIGN_PLACEHOLDER);
            }
        }
    }

    /**
     * 如果客户端ip已经存在，不再设置
     * @param clientIp
     */
    public void setClientIp(String clientIp) {
        Object client = getLayerBy(FrameConstant.CLIENT_IP);
        if(client == null) {
            setLayerBy(FrameConstant.CLIENT_IP, clientIp);
        }
    }

    public void setServerIp(String serverIp) {
        Object server = getLayerBy(FrameConstant.SERVER_IP);
        if(server == null) {
            setLayerBy(FrameConstant.SERVER_IP, serverIp);
        }
    }

    public void setTcpConnectionSuccess() {
        setLayerBy(FrameConstant.TCP_CONNECTION_SUCCESS, FrameConstant.SIGN_PLACEHOLDER);
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

    public void setTcpUpPayload() {
        setLayerBy(FrameConstant.TCP_UP_PAYLOAD, FrameConstant.SIGN_PLACEHOLDER);
    }

    public void setTcpDownPayload() {
        setLayerBy(FrameConstant.TCP_DOWN_PAYLOAD, FrameConstant.SIGN_PLACEHOLDER);
    }

    public void setTcpUpRetrans() {
        setLayerBy(FrameConstant.TCP_UP_RETRANS, FrameConstant.SIGN_PLACEHOLDER);
    }

    public void setTcpDownRetrans() {
        setLayerBy(FrameConstant.TCP_DOWN_RETRANS, FrameConstant.SIGN_PLACEHOLDER);
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

    public void delClientIp() {
        delLayerBy(FrameConstant.CLIENT_IP);
    }

    public void delServerIp() {
        delLayerBy(FrameConstant.SERVER_IP);
    }

    public void delTcpConnectionSuccess() {
        delLayerBy(FrameConstant.TCP_CONNECTION_SUCCESS);
    }

    private void setLayerBy(String metric, String value) {
        ((JSONObject) json.get(FrameConstant.LAYERS)).put(metric, value);
    }

    @SuppressWarnings("unchecked")
    private String getLayerFirstBy(String metric) {
        return ((List<String>) getLayerBy(metric)).get(0);
    }

    @SuppressWarnings("unchecked")
    private Integer getIntegerLayerFirstBy(String metric) {
        Object o = getLayerBy(metric);
        if (o != null) {
            return Integer.valueOf(((List<String>) o).get(0));
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
