package com.riil.ws.analysis.buf.map;

import com.alibaba.fastjson.annotation.JSONField;
import com.riil.ws.analysis.common.IpV4Util;

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

        private Integer ipProto;

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

        @JSONField(name = FrameConstant.TCP_ANALYSIS_DUPLICATE_ACK)
        private Boolean tcpAnalysisDuplicateAck;

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

        /**
         * 如果客户端ip已经存在，不再设置
         *
         * @param clientIp
         */
        public void setClientIp(String clientIp) {
            if (this.clientIpInt == null) {
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

        /**
         * 如果服务端ip已经存在，不再设置
         *
         * @param serverIp
         */
        public void setServerIp(String serverIp) {
            if (this.serverIpInt == null) {
                this.serverIpInt = IpV4Util.ipStr2Int(serverIp);
            }
        }

        public Integer getIpProto() {
            return ipProto;
        }

        public void setIpProto(Integer ipProto) {
            this.ipProto = ipProto;
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

        public Boolean getTcp() {
            return tcp;
        }

        public void setTcp(Boolean tcp) {
            this.tcp = tcp;
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

        public Boolean getTcpAnalysisDuplicateAck() {
            return tcpAnalysisDuplicateAck;
        }

        public void setTcpAnalysisDuplicateAck(Boolean tcpAnalysisDuplicateAck) {
            this.tcpAnalysisDuplicateAck = tcpAnalysisDuplicateAck;
        }
    }
}
