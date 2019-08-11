package com.riil.ws.analysis.buf.map;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.support.spring.annotation.FastJsonFilter;

public class FrameBean {
    private String index;
    private long timestamp;
    private Boolean tcp;
    private Boolean tcpConnectionSyn;
    private String clientIp;
    private String serverIp;

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

    public Boolean getTcp() {
        return tcp;
    }

    public void setTcp(Boolean tcp) {
        this.tcp = tcp;
    }

    public Boolean getTcpConnectionSyn() {
        return tcpConnectionSyn;
    }

    public void setTcpConnectionSyn(Boolean tcpConnectionSyn) {
        this.tcpConnectionSyn = tcpConnectionSyn;
    }

    public String getClientIp() {
        return clientIp;
    }

    /**
     * 如果客户端ip已经存在，不再设置
     * @param clientIp
     */
    public void setClientIp(String clientIp) {
        if(this.clientIp == null) {
            this.clientIp = clientIp;
        }
    }

    public String getServerIp() {
        return serverIp;
    }

    /**
     * 如果服务端ip已经存在，不再设置
     * @param serverIp
     */
    public void setServerIp(String serverIp) {
        if(this.serverIp == null) {
            this.serverIp = serverIp;
        }
    }

    public Layers getLayers() {
        return layers;
    }

    public class Layers {
        @JSONField(name=FrameConstant.IP_SRC)
        private String srcIp;

        public String getSrcIp() {
            return srcIp;
        }

        public void setSrcIp(String srcIp) {
            this.srcIp = srcIp;
        }
    }
}
