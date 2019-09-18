package com.riil.ws.analysis.buf.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.riil.ws.analysis.buf.map.AnalyzerConstant.*;

@Service
public class Adapter {
    private final Logger LOGGER = LoggerFactory.getLogger(Adapter.class);
    private JSONObject indexJson;
    private JSONObject json;
    private FrameBean frame;

    @Value("${output.es.index}")
    private String index;

    public FrameBean esJson2Bean(String esIndexJson, String frameJson) {
        frame = new FrameBean();
        indexJson = JSON.parseObject(esIndexJson);
        setIndex();

        json = JSON.parseObject(frameJson);
        setTimeStamp();
        setFrameNumber();
        setFrameLen();
        setFrameProto();
        setIpFragment();
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
        setUdpStream();
        setUdpSrcPort();
        setUdpDstPort();
        setDnsFlagsResponse();
        setDnsQryHost();
        setDnsFlagsRcode();
        setDnsQryName();
        setDnsAnswerIp();
        setIcmpType();
        setIcmpCode();

        return frame;
    }

    public String esBean2IndexJson(FrameBean frame) {
        return generateIndexJson(frame.getIndex());
    }

    public String esBean2FrameJson(FrameBean frame) {
        return JSON.toJSONString(frame);
    }

    private void setIndex() {
        if (StringUtils.isEmpty(index)) {
            frame.setIndex((String) ((JSONObject) indexJson.get(INDEX)).get(_INDEX));
        } else {
            frame.setIndex(index.trim());
        }
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

    @SuppressWarnings("unchecked")
    private void setFrameProto() {
        Object objs = getLayerBy(FrameConstant.IP_PROTO);
        if (objs != null) {
            List<String> protos = (List<String>) objs;
            List<Integer> ipProtos = new ArrayList<>(protos.size());
            for (String proto : protos) {
                ipProtos.add(Integer.valueOf(proto));
            }
            frame.getLayers().setIpProto(ipProtos);
        }
    }

    @SuppressWarnings("unchecked")
    private void setIpFragment() {
        Object objs = getLayerBy(FrameConstant.IP_FRAGMENT);
        if (objs != null) {
            List<String> fragments = (List<String>) objs;
            List<Integer> ipFragments = new ArrayList<>(fragments.size());
            for (String fragment : fragments) {
                ipFragments.add(Integer.valueOf(fragment));
            }
            frame.getLayers().setIpFragment(ipFragments);
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
        frame.getLayers().setTcpFirstSegment(getIntegerLayerFirstBy(FrameConstant.TCP_SEGMENT));
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
    private void setUdpStream() {
        frame.getLayers().setUdpStream(getIntegerLayerFirstBy(FrameConstant.UDP_STREAM));
    }

    private void setUdpSrcPort() {
        frame.getLayers().setUdpSrcPort(getIntegerLayerFirstBy(FrameConstant.UDP_SRCPORT));
    }

    private void setUdpDstPort() {
        frame.getLayers().setUdpDstPort(getIntegerLayerFirstBy(FrameConstant.UDP_DSTPORT));
    }

    private void setDnsFlagsResponse() {
        frame.getLayers().setDnsFlagsResponse(getIntegerLayerFirstBy(FrameConstant.DNS_FLAGS_RESPONSE));
    }

    /**
     * DNS的查询类型是 1 ， 表示查询主机。
     */
    private void setDnsQryHost() {
        Integer qryType = getIntegerLayerFirstBy(FrameConstant.DNS_QRY_TYPE);
        if (qryType != null) {
            if (qryType.equals(FrameConstant.DNS_QRY_TYPE_HOST)) {
                frame.getLayers().setDnsQryHost(true);
            }
        }
    }

    private void setDnsFlagsRcode() {
        frame.getLayers().setDnsFlagsRcode(getIntegerLayerFirstBy(FrameConstant.DNS_FLAGS_RCODE));
    }

    private void setDnsQryName() {
        frame.getLayers().setDnsQryName(getLayerFirstBy(FrameConstant.DNS_QRY_NAME));
    }

    @SuppressWarnings("unchecked")
    private void setDnsAnswerIp() {
        // 过滤掉 非 DNS 查询 主机
        if (!Boolean.TRUE.equals(frame.getLayers().getDnsQryHost())) {
            return;
        }

        // 过滤掉 响应 不成功的
        if (!Integer.valueOf(FrameConstant.DNS_FLAGS_RCODE_NO_ERROR).equals(frame.getLayers().getDnsFlagsRcode())) {
            return;
        }

        // DNS Answer RRS 的个数
        Integer answers = getIntegerLayerFirstBy(FrameConstant.DNS_COUNT_ANSWERS);
        if (answers != null && answers > 0) {
            // DNS的 Answer RRs、Authority RRs、Additional RRs的类型
            List<String> respTypes = (List<String>) getLayerBy(FrameConstant.DNS_RESP_TYPE);
            // RRs中的所有是IP的
            List<String> dnsAs = (List<String>) getLayerBy(FrameConstant.DNS_A);
            // 应答是成功的，但是应答中没有ip
            if (CollectionUtils.isEmpty(dnsAs)) {
                LOGGER.debug("FrameNumber:" + frame.getLayers().getFrameNumber() + ", UdpStream:" + frame.getLayers().getUdpStream()
                        + ", Answers RRs have no ip.");
                return;
            }
            Iterator<String> dnsAIt = dnsAs.iterator();
            for (int i = 0; i < answers; i++) {
                if (respTypes.get(i).equals(String.valueOf(FrameConstant.DNS_RESP_TYPE_HOST_ADDRESS))) {
                    frame.getLayers().addAnswerIp(dnsAIt.next());
                }
            }
        }
    }

    private void setIcmpType() {
        frame.getLayers().setIcmpType(getShortLayerFirstBy(FrameConstant.ICMP_TYPE));
    }

    private void setIcmpCode() {
        frame.getLayers().setIcmpCode(getShortLayerFirstBy(FrameConstant.ICMP_CODE));
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
    private Short getShortLayerFirstBy(String metric) {
        Object o = getLayerBy(metric);
        if (o != null) {
            return Short.valueOf(((List<String>) o).get(0));
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
