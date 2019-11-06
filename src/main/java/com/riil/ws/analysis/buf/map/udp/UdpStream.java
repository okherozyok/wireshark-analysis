package com.riil.ws.analysis.buf.map.udp;

import com.riil.ws.analysis.buf.map.FrameBean;

import java.util.ArrayList;
import java.util.List;

public class UdpStream {
    private int udpStreamNumber;
    private Long dnsQryTime = null;
    private String clientIp = null;
    private String serverIp = null;

    private Integer clientPort = null;
    private Integer serverPort = null;

    private Boolean hasDnsResult = null;

    private List<FrameBean> frames;

    public UdpStream(int udpStreamNumber) {
        this.udpStreamNumber = udpStreamNumber;
        frames = new ArrayList<>();
    }

    public int getUdpStreamNumber() {
        return udpStreamNumber;
    }

    public void append(FrameBean frame) {
        frames.add(frame);
    }

    public List<FrameBean> getFrames() {
        return frames;
    }

    public Long getDnsQryTime() {
        return dnsQryTime;
    }

    public void setDnsQryTime(Long dnsQryTime) {
        this.dnsQryTime = dnsQryTime;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
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

    public Boolean isHasDnsResult() {
        return hasDnsResult;
    }

    public void setHasDnsResult(boolean hasDnsResult) {
        this.hasDnsResult = hasDnsResult;
    }
}
