package com.riil.ws.analysis.buf.map.tcp;

import com.alibaba.fastjson.annotation.JSONField;
import com.riil.ws.analysis.buf.map.FrameConstant;
import com.riil.ws.analysis.common.IpV4Util;

public abstract class AbstractMetricBean {
    @JSONField(serialize = false)
    private String index;
    private int tcpStream;
    private long timestamp;
    private int clientIpInt = -1;
    private int serverIpInt = -1;
    @JSONField(name = FrameConstant.CLIENT_PORT)
    private int clientPort = -1;
    @JSONField(name = FrameConstant.SERVER_PORT)
    private int serverPort = -1;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getTcpStream() {
        return tcpStream;
    }

    public void setTcpStream(int tcpStream) {
        this.tcpStream = tcpStream;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @JSONField(name = FrameConstant.CLIENT_IP)
    public String getClientIp() {
        return IpV4Util.ipInt2Str(clientIpInt);
    }

    public void setClientIp(String clientIp) {
        if (clientIpInt != -1) {
            return;
        }
        this.clientIpInt = IpV4Util.ipStr2Int(clientIp);
    }

    @JSONField(name = FrameConstant.SERVER_IP)
    public String getServerIp() {
        return IpV4Util.ipInt2Str(serverIpInt);
    }

    public void setServerIp(String serverIp) {
        if (serverIpInt != -1) {
            return;
        }
        this.serverIpInt = IpV4Util.ipStr2Int(serverIp);
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        if (this.clientPort != -1) {
            return;
        }
        this.clientPort = clientPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        if (this.serverPort != -1) {
            return;
        }
        this.serverPort = serverPort;
    }
}
