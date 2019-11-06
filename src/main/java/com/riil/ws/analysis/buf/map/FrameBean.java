package com.riil.ws.analysis.buf.map;

import com.alibaba.fastjson.annotation.JSONField;
import com.riil.ws.analysis.common.IpV4Util;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class FrameBean {
    @JSONField(serialize = false)
    private String index;
    private long timestamp;

    private Layers layers = new Layers();

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @JSONField(serialize = false)
    public Boolean isTcpLenBt0() {
        Integer tcpLen = layers.getTcpLen();
        if (tcpLen != null) {
            return tcpLen > 0;
        }

        return null;
    }

    @JSONField(serialize = false)
    public boolean isTcpConnectionSyn() {
        Boolean tcpConnectionSyn = layers.getTcpConnectionSyn();
        return tcpConnectionSyn == null ? false : tcpConnectionSyn;
    }

    @JSONField(serialize = false)
    public boolean isTcpConnectionSack() {
        Boolean tcpConnectionSack = layers.getTcpConnectionSack();
        return tcpConnectionSack == null ? false : tcpConnectionSack;
    }

    @JSONField(serialize = false)
    public boolean isTcpConnectionRst() {
        Boolean tcpConnectionRst = layers.getTcpConnectionRst();
        return tcpConnectionRst == null ? false : tcpConnectionRst;
    }

    @JSONField(serialize = false)
    public boolean isTcpConnectionFin() {
        Boolean tcpConnectionFin = layers.getTcpConnectionFin();
        return tcpConnectionFin == null ? false : tcpConnectionFin;
    }

    @JSONField(serialize = false)
    public boolean isTcpKeepAlive() {
        Boolean tcpAnalysisKeepAlive = layers.getTcpAnalysisKeepAlive();
        return tcpAnalysisKeepAlive == null ? false : tcpAnalysisKeepAlive;
    }

    @JSONField(serialize = false)
    public boolean isTcpZeroWindow() {
        Boolean tcpZeroWindow = layers.getTcpAnalysisZeroWindow();
        return tcpZeroWindow == null ? false : tcpZeroWindow;
    }

    @JSONField(serialize = false)
    public boolean isRetrans() {
        Boolean tcpAnalysisRetransmission = layers.getTcpAnalysisRetransmission();
        return tcpAnalysisRetransmission == null ? false : tcpAnalysisRetransmission;
    }

    @JSONField(serialize = false)
    public boolean isHttpRequest() {
        Boolean httpRequest = layers.getHttpRequest();
        return httpRequest == null ? false : httpRequest;
    }

    @JSONField(serialize = false)
    public boolean isHttpResponse() {
        Boolean httpResponse = layers.getHttpResponse();
        return httpResponse == null ? false : httpResponse;
    }

    @JSONField(serialize = false)
    public boolean isDnsQry() {
        Integer dnsFlagsResponse = layers.getDnsFlagsResponse();
        if (dnsFlagsResponse == null) {
            return false;
        }
        return dnsFlagsResponse == 0 ? true : false;
    }

    public boolean containsIcmp() {
        List<Integer> ipProto = layers.getIpProto();
        if (ipProto == null) {
            return false;
        }

        return ipProto.contains(FrameConstant.ICMP_PROTO_NUM);
    }

    @JSONField(serialize = false)
    public int getFrameNumber() {
        return layers.getFrameNumber();
    }

    @JSONField(serialize = false)
    public int getFrameLength() {
        return layers.getFrameLen();
    }

    @JSONField(serialize = false)
    public Integer getTcpStreamNumber() {
        return layers.getTcpStream();
    }

    @JSONField(serialize = false)
    public Integer getTcpAckFrameNumber() {
        return layers.getTcpAnalysisAcksFrame();
    }

    @JSONField(serialize = false)
    public Long getTcpAnalysisAckRtt() {
        Float tcpAnalysisAckRtt = layers.getTcpAnalysisAckRtt();
        BigDecimal bd = new BigDecimal(String.valueOf(tcpAnalysisAckRtt));
        return bd.setScale(3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1000L)).longValue();
    }

    @JSONField(serialize = false)
    public Integer getTcpAnalysisDuplicateAckFrame() {
        return layers.getTcpAnalysisDuplicateAckFrame();
    }

    @JSONField(serialize = false)
    public Integer getTcpAnalysisDuplicateAckNum() {
        return layers.getTcpAnalysisDuplicateAckNum();
    }

    @JSONField(serialize = false)
    public String getSrcIp() {
        return layers.getIpSrc();
    }

    @JSONField(serialize = false)
    public String getDstIp() {
        return layers.getIpDst();
    }

    @JSONField(serialize = false)
    public List<Integer> getIpFragment() {
        return layers.getIpFragment();
    }

    @JSONField(serialize = false)
    public Integer getTcpSrcPort() {
        return layers.getTcpSrcPort();
    }

    @JSONField(serialize = false)
    public Integer getUdpDstPort() {
        return layers.getUdpDstPort();
    }

    @JSONField(serialize = false)
    public Integer getUdpSrcPort() {
        return layers.getUdpSrcPort();
    }

    @JSONField(serialize = false)
    public Integer getTcpDstPort() {
        return layers.getTcpDstPort();
    }

    @JSONField(serialize = false)
    public Integer getFirstTcpSegmentIfHas() {
        return layers.getTcpFirstSegment();
    }

    @JSONField(serialize = false)
    public Integer getHttpRequestIn() {
        return layers.getHttpRequestIn();
    }

    @JSONField(serialize = false)
    public Integer getUdpStreamNumber() {
        return layers.getUdpStream();
    }

    @JSONField(serialize = false)
    public Boolean getDnsQryHost() {
        return layers.getDnsQryHost();
    }

    @JSONField(serialize = false)
    public Integer getDnsFlagsRcode() {
        return layers.getDnsFlagsRcode();
    }

    @JSONField(serialize = false)
    public List<String> getDnsAnswerIp() {
        return layers.getDnsAnswerIp();
    }

    @JSONField(serialize = false)
    public Short getIcmpType() {
        return layers.getIcmpType();
    }

    @JSONField(serialize = false)
    public Short getIcmpCode() {
        return layers.getIcmpCode();
    }

    /**
     * 如果客户端ip已经存在，不再设置
     *
     * @param clientIp
     */
    public void setClientIp(String clientIp) {
        if (layers.getClientIp() == null) {
            layers.setClientIp(clientIp);
        }
    }

    /**
     * 如果服务端ip已经存在，不再设置
     *
     * @param serverIp
     */
    public void setServerIp(String serverIp) {
        if (layers.getServerIp() == null) {
            layers.setServerIp(serverIp);
        }
    }

    /**
     * 如果客户端port已经存在，不再设置
     *
     * @param port
     */
    public void setClientPort(Integer port) {
        if (layers.getClientPort() == null) {
            layers.setClientPort(port);
        }
    }

    /**
     * 如果服务端port已经存在，不再设置
     *
     * @param port
     */
    public void setServerPort(Integer port) {
        if (layers.getServerPort() == null) {
            layers.setServerPort(port);
        }
    }

    public void setOnlineUser(String onlineUser) {
        layers.setOnlineUser(onlineUser);
    }

    public void setTcpConnectionSuccess() {
        layers.setTcpConnectionSuccess(true);
    }

    public void setTcpClientConnectionRst() {
        layers.setTcpClientConnectionRst(true);
    }

    public void setTcpServerConnectionRst() {
        layers.setTcpServerConnectionRst(true);
    }

    public void setTcpClientConnectionNoResp() {
        layers.setTcpClientConnectionNoResp(true);
    }

    public void setTcpServerConnectionNoResp() {
        layers.setTcpServerConnectionNoResp(true);
    }

    public void setTcpDisConnectionNormal() {
        layers.setTcpDisconnectionNormal(true);
    }

    public void setTcpClientDisconnectionRst() {
        layers.setTcpClientDisconnectionRst(true);
    }

    public void setTcpServerDisconnectionRst() {
        layers.setTcpServerDisconnectionRst(true);
    }

    public void setTcpClientDisconnectionFinRst() {
        layers.setTcpClientDisconnectionFinRst(true);
    }

    public void setTcpServerDisconnectionFinRst() {
        layers.setTcpServerDisconnectionFinRst(true);
    }

    public void setTcpClientDisconnectionFinNoResp() {
        layers.setTcpClientDisconnectionFinNoResp(true);
    }

    public void setTcpServerDisconnectionFinNoResp() {
        layers.setTcpServerDisconnectionFinNoResp(true);
    }

    public void setTcpClientConnectionDelay(Long delay) {
        layers.setTcpClientConnectionDelay(delay);
    }

    public void setTcpServerConnectionDelay(Long delay) {
        layers.setTcpServerConnectionDelay(delay);
    }

    public void setTcpConnectionDelay(Long delay) {
        layers.setTcpConnectionDelay(delay);
    }

    public void setTcpUpRTT(Long rtt) {
        layers.setTcpUpRtt(rtt);
    }

    public void setTcpDownRTT(Long rtt) {
        layers.setTcpDownRtt(rtt);
    }

    public void setTcpUpPayload() {
        layers.setTcpUpPayload(true);
    }

    public void setTcpDownPayload() {
        layers.setTcpDownPayload(true);
    }

    public void setTcpUpRetrans() {
        layers.setTcpUpRetrans(true);
    }

    public void setTcpDownRetrans() {
        layers.setTcpDownRetrans(true);
    }

    public void setTcpClientDupAck(Integer num) {
        layers.setTcpClientDupAck(num);
    }

    public void setTcpServerDupAck(Integer num) {
        layers.setTcpServerDupAck(num);
    }

    public void setTcpClientZeroWindow(Integer num) {
        layers.setTcpClientZeroWindow(num);
    }

    public void setTcpServerZeroWindow(Integer num) {
        layers.setTcpServerZeroWindow(num);
    }

    public void setHttpReqTransDelay(Long delay) {
        layers.setHttpReqTransDelay(delay);
    }

    public void setHttpRespDelay(Long delay) {
        layers.setHttpRespDelay(delay);
    }

    public void setHttpRespTransDelay(Long delay) {
        layers.setHttpRespTransDelay(delay);
    }

    public void setDnsReplyDelay(Long delay) {
        layers.setDnsReplyDelay(delay);
    }

    public void setDnsQrySuccess() {
        layers.setDnsQrySuccess(true);
    }

    public void setDnsErrorAnswer() {
        layers.setDnsErrorAnswer(true);
    }

    public void setDnsNoResponse() {
        layers.setDnsNoResponse(true);
    }

    public void setDnsServerRespNoIp() {
        layers.setDnsServerRespNoIp(true);
    }

    public void delClientIp() {
        layers.setClientIp(null);
    }

    public void delServerIp() {
        layers.setServerIp(null);
    }

    public void delTcpConnectionSuccess() {
        layers.setTcpConnectionSuccess(null);
    }

    public Layers getLayers() {
        return layers;
    }

    public class Layers {
        @JSONField(name = FrameConstant.FRAME_NUMBER)
        private Integer frameNumber;

        @JSONField(name = FrameConstant.FRAME_LEN)
        private Integer frameLen;

        private Integer srcIpInt;
        private Integer dstIpInt;

        private Integer clientIpInt;
        private Integer serverIpInt;

        @JSONField(name = FrameConstant.CLIENT_PORT)
        private Integer clientPort;

        @JSONField(name = FrameConstant.SERVER_PORT)
        private Integer serverPort;

        private Integer onlineUserInt;

        @JSONField(name = FrameConstant.IP_PROTO)
        private List<Integer> ipProto;

        @JSONField(name = FrameConstant.IP_FRAGMENT)
        private List<Integer> ipFragment;

        @JSONField(name = FrameConstant.TCP)
        private Boolean tcp;

        @JSONField(name = FrameConstant.TCP_SRCPORT)
        private Integer tcpSrcPort;

        @JSONField(name = FrameConstant.TCP_DSTPORT)
        private Integer tcpDstPort;

        @JSONField(name = FrameConstant.TCP_STREAM)
        private Integer tcpStream;

        @JSONField(name = FrameConstant.TCP_SEQ)
        private Long tcpSeq;

        @JSONField(name = FrameConstant.TCP_ACK)
        private Long tcpAck;

        @JSONField(name = FrameConstant.TCP_LEN)
        private Integer tcpLen;

        @JSONField(name = FrameConstant.TCP_CONNECTION_SYN)
        private Boolean tcpConnectionSyn;

        @JSONField(name = FrameConstant.TCP_CONNECTION_SACK)
        private Boolean tcpConnectionSack;

        @JSONField(name = FrameConstant.TCP_CONNECTION_FIN)
        private Boolean tcpConnectionFin;

        @JSONField(name = FrameConstant.TCP_CONNECTION_RST)
        private Boolean tcpConnectionRst;

        @JSONField(name = FrameConstant.TCP_ANALYSIS_ACKS_FRAME)
        private Integer tcpAnalysisAcksFrame;

        @JSONField(name = FrameConstant.TCP_ANALYSIS_ACK_RTT)
        private Float tcpAnalysisAckRtt;

        @JSONField(name = FrameConstant.TCP_ANALYSIS_DUPLICATE_ACK_FRAME)
        private Integer tcpAnalysisDuplicateAckFrame;

        @JSONField(name = FrameConstant.TCP_ANALYSIS_DUPLICATE_ACK_NUM)
        private Integer tcpAnalysisDuplicateAckNum;

        @JSONField(name = FrameConstant.TCP_FIRST_SEGMENT)
        private Integer tcpFirstSegment;

        @JSONField(name = FrameConstant.TCP_ANALYSIS_RETRANSMISSION)
        private Boolean tcpAnalysisRetransmission;

        @JSONField(name = FrameConstant.TCP_ANALYSIS_KEEP_ALIVE)
        private Boolean tcpAnalysisKeepAlive;

        @JSONField(name = FrameConstant.TCP_ANALYSIS_ZERO_WINDOW)
        private Boolean tcpAnalysisZeroWindow;

        @JSONField(name = FrameConstant.TCP_CONNECTION_SUCCESS)
        private Boolean tcpConnectionSuccess;

        @JSONField(name = FrameConstant.TCP_CLIENT_CONNECTION_RST)
        private Boolean tcpClientConnectionRst;

        @JSONField(name = FrameConstant.TCP_SERVER_CONNECTION_RST)
        private Boolean tcpServerConnectionRst;

        @JSONField(name = FrameConstant.TCP_CLIENT_CONNECTION_NO_RESP)
        private Boolean tcpClientConnectionNoResp;

        @JSONField(name = FrameConstant.TCP_SERVER_CONNECTION_NO_RESP)
        private Boolean tcpServerConnectionNoResp;

        @JSONField(name = FrameConstant.TCP_DISCONNECTION_NORMAL)
        private Boolean tcpDisconnectionNormal;

        @JSONField(name = FrameConstant.TCP_CLIENT_DISCONNECTION_RST)
        private Boolean tcpClientDisconnectionRst;

        @JSONField(name = FrameConstant.TCP_SERVER_DISCONNECTION_RST)
        private Boolean tcpServerDisconnectionRst;

        @JSONField(name = FrameConstant.TCP_CLIENT_DISCONNECTION_FIN_RST)
        private Boolean tcpClientDisconnectionFinRst;

        @JSONField(name = FrameConstant.TCP_SERVER_DISCONNECTION_FIN_RST)
        private Boolean tcpServerDisconnectionFinRst;

        @JSONField(name = FrameConstant.TCP_CLIENT_DISCONNECTION_FIN_NO_RESP)
        private Boolean tcpClientDisconnectionFinNoResp;

        @JSONField(name = FrameConstant.TCP_SERVER_DISCONNECTION_FIN_NO_RESP)
        private Boolean tcpServerDisconnectionFinNoResp;

        @JSONField(name = FrameConstant.TCP_CLIENT_CONNECTION_DELAY)
        private Long tcpClientConnectionDelay;

        @JSONField(name = FrameConstant.TCP_SERVER_CONNECTION_DELAY)
        private Long tcpServerConnectionDelay;

        @JSONField(name = FrameConstant.TCP_CONNECTION_DELAY)
        private Long tcpConnectionDelay;

        @JSONField(name = FrameConstant.TCP_UP_RTT)
        private Long tcpUpRtt;

        @JSONField(name = FrameConstant.TCP_DOWN_RTT)
        private Long tcpDownRtt;

        @JSONField(name = FrameConstant.TCP_UP_PAYLOAD)
        private Boolean tcpUpPayload;

        @JSONField(name = FrameConstant.TCP_DOWN_PAYLOAD)
        private Boolean tcpDownPayload;

        @JSONField(name = FrameConstant.TCP_UP_RETRANS)
        private Boolean tcpUpRetrans;

        @JSONField(name = FrameConstant.TCP_DOWN_RETRANS)
        private Boolean tcpDownRetrans;

        @JSONField(name = FrameConstant.TCP_CLIENT_DUP_ACK)
        private Integer tcpClientDupAck;

        @JSONField(name = FrameConstant.TCP_SERVER_DUP_ACK)
        private Integer tcpServerDupAck;

        @JSONField(name = FrameConstant.TCP_CLIENT_ZERO_WINDOW)
        private Integer tcpClientZeroWindow;

        @JSONField(name = FrameConstant.TCP_SERVER_ZERO_WINDOW)
        private Integer tcpServerZeroWindow;

        @JSONField(name = FrameConstant.HTTP_REQUEST)
        private Boolean httpRequest;

        @JSONField(name = FrameConstant.HTTP_RESPONSE)
        private Boolean httpResponse;

        @JSONField(name = FrameConstant.HTTP_REQUEST_IN)
        private Integer httpRequestIn;

        @JSONField(name = FrameConstant.HTTP_RESPONSE_CODE)
        private Integer httpResponseCode;

        @JSONField(name = FrameConstant.HTTP_REQ_TRANS_DELAY)
        private Long httpReqTransDelay;

        @JSONField(name = FrameConstant.HTTP_RESP_DELAY)
        private Long httpRespDelay;

        @JSONField(name = FrameConstant.HTTP_RESP_TRANS_DELAY)
        private Long httpRespTransDelay;

        @JSONField(name = FrameConstant.UDP_STREAM)
        private Integer udpStream;

        @JSONField(name = FrameConstant.UDP_SRCPORT)
        private Integer udpSrcPort;

        @JSONField(name = FrameConstant.UDP_DSTPORT)
        private Integer udpDstPort;

        @JSONField(name = FrameConstant.DNS_FLAGS_RESPONSE)
        private Integer dnsFlagsResponse;

        @JSONField(name = FrameConstant.DNS_QRY_HOST)
        private Boolean dnsQryHost;

        @JSONField(name = FrameConstant.DNS_FLAGS_RCODE)
        private Integer dnsFlagsRcode;

        @JSONField(name = FrameConstant.DNS_QRY_NAME)
        private String dnsQryName;

        private List<Integer> dnsAnswerIp = null;

        @JSONField(name = FrameConstant.DNS_REPLY_DELAY)
        private Long dnsReplyDelay;

        @JSONField(name = FrameConstant.DNS_QRY_SUCCESS)
        private Boolean dnsQrySuccess;

        @JSONField(name = FrameConstant.DNS_ERROR_ANSWER)
        private Boolean dnsErrorAnswer;

        @JSONField(name = FrameConstant.DNS_NO_RESPONSE)
        private Boolean dnsNoResponse;

        @JSONField(name = FrameConstant.DNS_SERVER_RESP_NO_IP)
        private Boolean dnsServerRespNoIp;

        @JSONField(name = FrameConstant.ICMP_TYPE)
        private Short icmpType;

        @JSONField(name = FrameConstant.ICMP_CODE)
        private Short icmpCode;

        public void setSrcIp(String srcIp) {
            this.srcIpInt = IpV4Util.ipStr2Int(srcIp);
        }

        @JSONField(name = FrameConstant.IP_SRC)
        public String getIpSrc() {
            return IpV4Util.ipInt2Str(srcIpInt);
        }

        public void setDstIp(String dstIp) {
            this.dstIpInt = IpV4Util.ipStr2Int(dstIp);
        }

        @JSONField(name = FrameConstant.IP_DST)
        public String getIpDst() {
            return IpV4Util.ipInt2Str(dstIpInt);
        }

        @JSONField(name = FrameConstant.CLIENT_IP)
        public String getClientIp() {
            if (clientIpInt != null) {
                return IpV4Util.ipInt2Str(clientIpInt);
            }

            return null;
        }


        public void setClientIp(String clientIp) {
            if (clientIp == null) {
                this.clientIpInt = null;
            } else {
                this.clientIpInt = IpV4Util.ipStr2Int(clientIp);
            }
        }

        @JSONField(name = FrameConstant.SERVER_IP)
        public String getServerIp() {
            if (serverIpInt != null) {
                return IpV4Util.ipInt2Str(serverIpInt);
            }

            return null;
        }

        public void setServerIp(String serverIp) {
            if (serverIp == null) {
                this.serverIpInt = null;
            } else {
                this.serverIpInt = IpV4Util.ipStr2Int(serverIp);
            }
        }

        public Integer getClientPort() {
            return clientPort;
        }

        public void setClientPort(Integer clientPort) {
            this.clientPort = clientPort;
        }

        public Integer getServerPort() {
            return serverPort;
        }

        public void setServerPort(Integer serverPort) {
            this.serverPort = serverPort;
        }

        @JSONField(name = FrameConstant.ONLINE_USER)
        public String getOnlineUser() {
            if (onlineUserInt != null) {
                return IpV4Util.ipInt2Str(onlineUserInt);
            }

            return null;
        }

        public void setOnlineUser(String onlineUser) {
            if (onlineUser == null) {
                onlineUserInt = null;
            } else {
                onlineUserInt = IpV4Util.ipStr2Int(onlineUser);
            }
        }

        public List<Integer> getIpProto() {
            return ipProto;
        }

        public void setIpProto(List<Integer> ipProto) {
            this.ipProto = ipProto;
        }

        public List<Integer> getIpFragment() {
            return ipFragment;
        }

        public void setIpFragment(List<Integer> ipFragment) {
            this.ipFragment = ipFragment;
        }

        public Integer getFrameNumber() {
            return frameNumber;
        }

        public void setFrameNumber(Integer frameNumber) {
            this.frameNumber = frameNumber;
        }

        public Integer getFrameLen() {
            return frameLen;
        }

        public void setFrameLen(Integer frameLen) {
            this.frameLen = frameLen;
        }

        public Integer getTcpSrcPort() {
            return tcpSrcPort;
        }

        public void setTcpSrcPort(Integer tcpSrcPort) {
            this.tcpSrcPort = tcpSrcPort;
        }

        public Integer getTcpDstPort() {
            return tcpDstPort;
        }

        public void setTcpDstPort(Integer tcpDstPort) {
            this.tcpDstPort = tcpDstPort;
        }

        public Integer getTcpStream() {
            return tcpStream;
        }

        public void setTcpStream(Integer tcpStream) {
            this.tcpStream = tcpStream;
        }

        public Long getTcpSeq() {
            return tcpSeq;
        }

        public void setTcpSeq(Long tcpSeq) {
            this.tcpSeq = tcpSeq;
        }

        public Long getTcpAck() {
            return tcpAck;
        }

        public void setTcpAck(Long tcpAck) {
            this.tcpAck = tcpAck;
        }

        public Integer getTcpLen() {
            return tcpLen;
        }

        public void setTcpLen(Integer tcpLen) {
            this.tcpLen = tcpLen;
        }

        public Boolean getTcpConnectionSyn() {
            return tcpConnectionSyn;
        }

        public void setTcpConnectionSyn(Boolean tcpConnectionSyn) {
            this.tcpConnectionSyn = tcpConnectionSyn;
        }

        public Boolean getTcpConnectionSack() {
            return tcpConnectionSack;
        }

        public void setTcpConnectionSack(Boolean tcpConnectionSack) {
            this.tcpConnectionSack = tcpConnectionSack;
        }

        public Boolean getTcpConnectionFin() {
            return tcpConnectionFin;
        }

        public void setTcpConnectionFin(Boolean tcpConnectionFin) {
            this.tcpConnectionFin = tcpConnectionFin;
        }

        public Boolean getTcpConnectionRst() {
            return tcpConnectionRst;
        }

        public void setTcpConnectionRst(Boolean tcpConnectionRst) {
            this.tcpConnectionRst = tcpConnectionRst;
        }

        public Integer getTcpAnalysisAcksFrame() {
            return tcpAnalysisAcksFrame;
        }

        public void setTcpAnalysisAcksFrame(Integer tcpAnalysisAcksFrame) {
            this.tcpAnalysisAcksFrame = tcpAnalysisAcksFrame;
        }

        public Float getTcpAnalysisAckRtt() {
            return tcpAnalysisAckRtt;
        }

        public void setTcpAnalysisAckRtt(Float tcpAnalysisAckRtt) {
            this.tcpAnalysisAckRtt = tcpAnalysisAckRtt;
        }

        public Integer getTcpAnalysisDuplicateAckFrame() {
            return tcpAnalysisDuplicateAckFrame;
        }

        public void setTcpAnalysisDuplicateAckFrame(Integer tcpAnalysisDuplicateAckFrame) {
            this.tcpAnalysisDuplicateAckFrame = tcpAnalysisDuplicateAckFrame;
        }

        public Integer getTcpAnalysisDuplicateAckNum() {
            return tcpAnalysisDuplicateAckNum;
        }

        public void setTcpAnalysisDuplicateAckNum(Integer tcpAnalysisDuplicateAckNum) {
            this.tcpAnalysisDuplicateAckNum = tcpAnalysisDuplicateAckNum;
        }

        public Integer getTcpFirstSegment() {
            return tcpFirstSegment;
        }

        public void setTcpFirstSegment(Integer tcpFirstSegment) {
            this.tcpFirstSegment = tcpFirstSegment;
        }

        public Boolean getTcpAnalysisRetransmission() {
            return tcpAnalysisRetransmission;
        }

        public void setTcpAnalysisRetransmission(Boolean tcpAnalysisRetransmission) {
            this.tcpAnalysisRetransmission = tcpAnalysisRetransmission;
        }

        public Boolean getTcpAnalysisKeepAlive() {
            return tcpAnalysisKeepAlive;
        }

        public void setTcpAnalysisKeepAlive(Boolean tcpAnalysisKeepAlive) {
            this.tcpAnalysisKeepAlive = tcpAnalysisKeepAlive;
        }

        public Boolean getTcpAnalysisZeroWindow() {
            return tcpAnalysisZeroWindow;
        }

        public void setTcpAnalysisZeroWindow(Boolean tcpAnalysisZeroWindow) {
            this.tcpAnalysisZeroWindow = tcpAnalysisZeroWindow;
        }

        public Boolean getTcpConnectionSuccess() {
            return tcpConnectionSuccess;
        }

        public void setTcpConnectionSuccess(Boolean tcpConnectionSuccess) {
            this.tcpConnectionSuccess = tcpConnectionSuccess;
        }

        public Boolean getTcpClientConnectionRst() {
            return tcpClientConnectionRst;
        }

        public void setTcpClientConnectionRst(Boolean tcpClientConnectionRst) {
            this.tcpClientConnectionRst = tcpClientConnectionRst;
        }

        public Boolean getTcpServerConnectionRst() {
            return tcpServerConnectionRst;
        }

        public void setTcpServerConnectionRst(Boolean tcpServerConnectionRst) {
            this.tcpServerConnectionRst = tcpServerConnectionRst;
        }

        public Boolean getTcpClientConnectionNoResp() {
            return tcpClientConnectionNoResp;
        }

        public void setTcpClientConnectionNoResp(Boolean tcpClientConnectionNoResp) {
            this.tcpClientConnectionNoResp = tcpClientConnectionNoResp;
        }

        public Boolean getTcpServerConnectionNoResp() {
            return tcpServerConnectionNoResp;
        }

        public void setTcpServerConnectionNoResp(Boolean tcpServerConnectionNoResp) {
            this.tcpServerConnectionNoResp = tcpServerConnectionNoResp;
        }

        public Boolean getTcpDisconnectionNormal() {
            return tcpDisconnectionNormal;
        }

        public void setTcpDisconnectionNormal(Boolean tcpDisconnectionNormal) {
            this.tcpDisconnectionNormal = tcpDisconnectionNormal;
        }

        public Boolean getTcpClientDisconnectionRst() {
            return tcpClientDisconnectionRst;
        }

        public void setTcpClientDisconnectionRst(Boolean tcpClientDisconnectionRst) {
            this.tcpClientDisconnectionRst = tcpClientDisconnectionRst;
        }

        public Boolean getTcpServerDisconnectionRst() {
            return tcpServerDisconnectionRst;
        }

        public void setTcpServerDisconnectionRst(Boolean tcpServerDisconnectionRst) {
            this.tcpServerDisconnectionRst = tcpServerDisconnectionRst;
        }

        public Boolean getTcpClientDisconnectionFinRst() {
            return tcpClientDisconnectionFinRst;
        }

        public void setTcpClientDisconnectionFinRst(Boolean tcpClientDisconnectionFinRst) {
            this.tcpClientDisconnectionFinRst = tcpClientDisconnectionFinRst;
        }

        public Boolean getTcpServerDisconnectionFinRst() {
            return tcpServerDisconnectionFinRst;
        }

        public void setTcpServerDisconnectionFinRst(Boolean tcpServerDisconnectionFinRst) {
            this.tcpServerDisconnectionFinRst = tcpServerDisconnectionFinRst;
        }

        public Boolean getTcpClientDisconnectionFinNoResp() {
            return tcpClientDisconnectionFinNoResp;
        }

        public void setTcpClientDisconnectionFinNoResp(Boolean tcpClientDisconnectionFinNoResp) {
            this.tcpClientDisconnectionFinNoResp = tcpClientDisconnectionFinNoResp;
        }

        public Boolean getTcpServerDisconnectionFinNoResp() {
            return tcpServerDisconnectionFinNoResp;
        }

        public void setTcpServerDisconnectionFinNoResp(Boolean tcpServerDisconnectionFinNoResp) {
            this.tcpServerDisconnectionFinNoResp = tcpServerDisconnectionFinNoResp;
        }

        public Long getTcpServerConnectionDelay() {
            return tcpServerConnectionDelay;
        }

        public void setTcpServerConnectionDelay(Long tcpServerConnectionDelay) {
            this.tcpServerConnectionDelay = tcpServerConnectionDelay;
        }

        public Long getTcpConnectionDelay() {
            return tcpConnectionDelay;
        }

        public void setTcpConnectionDelay(Long tcpConnectionDelay) {
            this.tcpConnectionDelay = tcpConnectionDelay;
        }

        public Long getTcpUpRtt() {
            return tcpUpRtt;
        }

        public void setTcpUpRtt(Long tcpUpRtt) {
            this.tcpUpRtt = tcpUpRtt;
        }

        public Long getTcpDownRtt() {
            return tcpDownRtt;
        }

        public void setTcpDownRtt(Long tcpDownRtt) {
            this.tcpDownRtt = tcpDownRtt;
        }

        public Boolean getTcpUpPayload() {
            return tcpUpPayload;
        }

        public void setTcpUpPayload(Boolean tcpUpPayload) {
            this.tcpUpPayload = tcpUpPayload;
        }

        public Boolean getTcpDownPayload() {
            return tcpDownPayload;
        }

        public void setTcpDownPayload(Boolean tcpDownPayload) {
            this.tcpDownPayload = tcpDownPayload;
        }

        public Boolean getTcpUpRetrans() {
            return tcpUpRetrans;
        }

        public void setTcpUpRetrans(Boolean tcpUpRetrans) {
            this.tcpUpRetrans = tcpUpRetrans;
        }

        public Boolean getTcpDownRetrans() {
            return tcpDownRetrans;
        }

        public void setTcpDownRetrans(Boolean tcpDownRetrans) {
            this.tcpDownRetrans = tcpDownRetrans;
        }

        public Integer getTcpClientDupAck() {
            return tcpClientDupAck;
        }

        public void setTcpClientDupAck(Integer tcpClientDupAck) {
            this.tcpClientDupAck = tcpClientDupAck;
        }

        public Integer getTcpServerDupAck() {
            return tcpServerDupAck;
        }

        public void setTcpServerDupAck(Integer tcpServerDupAck) {
            this.tcpServerDupAck = tcpServerDupAck;
        }

        public Integer getTcpClientZeroWindow() {
            return tcpClientZeroWindow;
        }

        public void setTcpClientZeroWindow(Integer tcpClientZeroWindow) {
            this.tcpClientZeroWindow = tcpClientZeroWindow;
        }

        public Integer getTcpServerZeroWindow() {
            return tcpServerZeroWindow;
        }

        public void setTcpServerZeroWindow(Integer tcpServerZeroWindow) {
            this.tcpServerZeroWindow = tcpServerZeroWindow;
        }

        public Long getTcpClientConnectionDelay() {
            return tcpClientConnectionDelay;
        }

        public void setTcpClientConnectionDelay(Long tcpClientConnectionDelay) {
            this.tcpClientConnectionDelay = tcpClientConnectionDelay;
        }

        public Boolean getHttpRequest() {
            return httpRequest;
        }

        public void setHttpRequest(Boolean httpRequest) {
            this.httpRequest = httpRequest;
        }

        public Boolean getHttpResponse() {
            return httpResponse;
        }

        public void setHttpResponse(Boolean httpResponse) {
            this.httpResponse = httpResponse;
        }

        public Integer getHttpRequestIn() {
            return httpRequestIn;
        }

        public void setHttpRequestIn(Integer httpRequestIn) {
            this.httpRequestIn = httpRequestIn;
        }

        public Integer getHttpResponseCode() {
            return httpResponseCode;
        }

        public void setHttpResponseCode(Integer httpResponseCode) {
            this.httpResponseCode = httpResponseCode;
        }

        public Long getHttpReqTransDelay() {
            return httpReqTransDelay;
        }

        public void setHttpReqTransDelay(Long httpReqTransDelay) {
            this.httpReqTransDelay = httpReqTransDelay;
        }

        public Long getHttpRespDelay() {
            return httpRespDelay;
        }

        public void setHttpRespDelay(Long httpRespDelay) {
            this.httpRespDelay = httpRespDelay;
        }

        public Long getHttpRespTransDelay() {
            return httpRespTransDelay;
        }

        public void setHttpRespTransDelay(Long httpRespTransDelay) {
            this.httpRespTransDelay = httpRespTransDelay;
        }

        public Integer getUdpStream() {
            return udpStream;
        }

        public void setUdpStream(Integer udpStream) {
            this.udpStream = udpStream;
        }

        public Integer getUdpSrcPort() {
            return udpSrcPort;
        }

        public void setUdpSrcPort(Integer udpSrcPort) {
            this.udpSrcPort = udpSrcPort;
        }

        public Integer getUdpDstPort() {
            return udpDstPort;
        }

        public void setUdpDstPort(Integer udpDstPort) {
            this.udpDstPort = udpDstPort;
        }

        public Integer getDnsFlagsResponse() {
            return dnsFlagsResponse;
        }

        public void setDnsFlagsResponse(Integer dnsFlagsResponse) {
            this.dnsFlagsResponse = dnsFlagsResponse;
        }

        public Boolean getDnsQryHost() {
            return dnsQryHost;
        }

        public void setDnsQryHost(Boolean dnsQryHost) {
            this.dnsQryHost = dnsQryHost;
        }

        public Integer getDnsFlagsRcode() {
            return dnsFlagsRcode;
        }

        public void setDnsFlagsRcode(Integer dnsFlagsRcode) {
            this.dnsFlagsRcode = dnsFlagsRcode;
        }

        public String getDnsQryName() {
            return dnsQryName;
        }

        public void setDnsQryName(String dnsQryName) {
            this.dnsQryName = dnsQryName;
        }

        public void addAnswerIp(String ip) {
            if (dnsAnswerIp == null) {
                dnsAnswerIp = new ArrayList<>();
            }

            dnsAnswerIp.add(IpV4Util.ipStr2Int(ip));
        }

        @JSONField(name = FrameConstant.DNS_ANSWER_IP)
        public List<String> getDnsAnswerIp() {
            if (!CollectionUtils.isEmpty(dnsAnswerIp)) {
                List<String> ips = new ArrayList<>(dnsAnswerIp.size());
                for (Integer ip : dnsAnswerIp) {
                    ips.add(IpV4Util.ipInt2Str(ip));
                }

                return ips;
            }

            return null;
        }

        public Long getDnsReplyDelay() {
            return dnsReplyDelay;
        }

        public void setDnsReplyDelay(Long dnsReplyDelay) {
            this.dnsReplyDelay = dnsReplyDelay;
        }

        public Boolean getDnsQrySuccess() {
            return dnsQrySuccess;
        }

        public void setDnsQrySuccess(Boolean dnsQrySuccess) {
            this.dnsQrySuccess = dnsQrySuccess;
        }

        public Boolean getDnsErrorAnswer() {
            return dnsErrorAnswer;
        }

        public void setDnsErrorAnswer(Boolean dnsErrorAnswer) {
            this.dnsErrorAnswer = dnsErrorAnswer;
        }

        public Boolean getDnsNoResponse() {
            return dnsNoResponse;
        }

        public void setDnsNoResponse(Boolean dnsNoResponse) {
            this.dnsNoResponse = dnsNoResponse;
        }

        public Boolean getDnsServerRespNoIp() {
            return dnsServerRespNoIp;
        }

        public void setDnsServerRespNoIp(Boolean dnsServerRespNoIp) {
            this.dnsServerRespNoIp = dnsServerRespNoIp;
        }

        public Short getIcmpType() {
            return icmpType;
        }

        public void setIcmpType(Short icmpType) {
            this.icmpType = icmpType;
        }

        public Short getIcmpCode() {
            return icmpCode;
        }

        public void setIcmpCode(Short icmpCode) {
            this.icmpCode = icmpCode;
        }
    }
}
