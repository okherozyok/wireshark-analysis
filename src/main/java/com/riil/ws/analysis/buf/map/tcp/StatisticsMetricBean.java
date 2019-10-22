package com.riil.ws.analysis.buf.map.tcp;

import com.alibaba.fastjson.annotation.JSONField;
import com.riil.ws.analysis.buf.map.FrameConstant;
import com.riil.ws.analysis.common.IpV4Util;

public class StatisticsMetricBean {
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

    @JSONField(name = FrameConstant.TCP_CLIENT_ZERO_WINDOW)
    private int tcpClientZeroWindow;
    @JSONField(name = FrameConstant.TCP_SERVER_ZERO_WINDOW)
    private int tcpServerZeroWindow;

    @JSONField(name = FrameConstant.TCP_FRAME_LEN_LESS_EQ_64)
    private int tcpFrameLenLessEq64;
    @JSONField(name = FrameConstant.TCP_FRAME_LEN_BETWEEN_65_511)
    private int tcpFrameLenBetween65_511;
    @JSONField(name = FrameConstant.TCP_FRAME_LEN_BETWEEN_512_1023)
    private int tcpFrameLenBetween512_1023;
    @JSONField(name = FrameConstant.TCP_FRAME_LEN_GREATER_EQ_1024)
    private int tcpFrameLenGreaterEq1024;

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

    public int getTcpClientZeroWindow() {
        return tcpClientZeroWindow;
    }

    public void setTcpClientZeroWindow(int tcpClientZeroWindow) {
        this.tcpClientZeroWindow = tcpClientZeroWindow;
    }

    public int getTcpServerZeroWindow() {
        return tcpServerZeroWindow;
    }

    public void setTcpServerZeroWindow(int tcpServerZeroWindow) {
        this.tcpServerZeroWindow = tcpServerZeroWindow;
    }

    public int getTcpFrameLenLessEq64() {
        return tcpFrameLenLessEq64;
    }

    public void setTcpFrameLenLessEq64(int tcpFrameLenLessEq64) {
        this.tcpFrameLenLessEq64 = tcpFrameLenLessEq64;
    }

    public int getTcpFrameLenBetween65_511() {
        return tcpFrameLenBetween65_511;
    }

    public void setTcpFrameLenBetween65_511(int tcpFrameLenBetween65_511) {
        this.tcpFrameLenBetween65_511 = tcpFrameLenBetween65_511;
    }

    public int getTcpFrameLenBetween512_1023() {
        return tcpFrameLenBetween512_1023;
    }

    public void setTcpFrameLenBetween512_1023(int tcpFrameLenBetween512_1023) {
        this.tcpFrameLenBetween512_1023 = tcpFrameLenBetween512_1023;
    }

    public int getTcpFrameLenGreaterEq1024() {
        return tcpFrameLenGreaterEq1024;
    }

    public void setTcpFrameLenGreaterEq1024(int tcpFrameLenGreaterEq1024) {
        this.tcpFrameLenGreaterEq1024 = tcpFrameLenGreaterEq1024;
    }
}
