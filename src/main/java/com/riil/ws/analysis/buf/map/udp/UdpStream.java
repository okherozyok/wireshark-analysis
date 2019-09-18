package com.riil.ws.analysis.buf.map.udp;

import com.riil.ws.analysis.buf.map.FrameBean;

import java.util.ArrayList;
import java.util.List;

public class UdpStream {
    private int udpStreamNumber;
    private Long dnsQryTime = null;
    private String clientIp = null;
    private String serverIp = null;

    // 专为流量区分客户端、服务端ip使用。
    // 不使用clientIp、serverIp的原因是与 Dns判断的clientIp、serverIp可能相反
    private String clientIpByFirst = null;
    private String serverIpByFirst = null;
    private Integer clientPortByFirst = null;
    private Integer serverPortByFirst = null;

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

    public String getClientIpByFirst() {
        return clientIpByFirst;
    }

    public void setClientIpByFirst(String clientIpByFirst) {
        this.clientIpByFirst = clientIpByFirst;
    }

    public String getServerIpByFirst() {
        return serverIpByFirst;
    }

    public void setServerIpByFirst(String serverIpByFirst) {
        this.serverIpByFirst = serverIpByFirst;
    }

    public Integer getClientPortByFirst() {
        return clientPortByFirst;
    }

    public void setClientPortByFirst(Integer clientPortByFirst) {
        this.clientPortByFirst = clientPortByFirst;
    }

    public Integer getServerPortByFirst() {
        return serverPortByFirst;
    }

    public void setServerPortByFirst(Integer serverPortByFirst) {
        this.serverPortByFirst = serverPortByFirst;
    }

    public Boolean isHasDnsResult() {
        return hasDnsResult;
    }

    public void setHasDnsResult(boolean hasDnsResult) {
        this.hasDnsResult = hasDnsResult;
    }
}
