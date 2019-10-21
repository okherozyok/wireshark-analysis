package com.riil.ws.analysis.buf.map;

import com.alibaba.fastjson.annotation.JSONField;
import com.riil.ws.analysis.common.IpV4Util;

public class StatisticsMetricBean {
    @JSONField(serialize = false)
    private String index;
    private Integer tcpStream;
    private long timestamp;

    private Integer clientIpInt;
    private Integer serverIpInt;

    @JSONField(name = FrameConstant.CLIENT_PORT)
    private Integer clientPort;
    @JSONField(name = FrameConstant.SERVER_PORT)
    private Integer serverPort;

    @JSONField(name = FrameConstant.TCP_CLIENT_ZERO_WINDOW)
    private Integer tcpClientZeroWindow;
    @JSONField(name = FrameConstant.TCP_SERVER_ZERO_WINDOW)
    private Integer tcpServerZeroWindow;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public Integer getTcpStream() {
        return tcpStream;
    }

    public void setTcpStream(Integer tcpStream) {
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
}
