package com.riil.ws.analysis.buf.map.dns;

import com.riil.ws.analysis.buf.map.FrameBean;

import java.util.ArrayList;
import java.util.List;

public class DnsSession {
    private int udpStreamNumber;
    private Long qryTime = null;
    private String clientIp = null;
    private String serverIp = null;
    private boolean hasResult = false;

    private List<FrameBean> frames;

    public DnsSession(int udpStreamNumber) {
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

    public Long getQryTime() {
        return qryTime;
    }

    public void setQryTime(Long qryTime) {
        this.qryTime = qryTime;
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

    public boolean isHasResult() {
        return hasResult;
    }

    public void setHasResult(boolean hasResult) {
        this.hasResult = hasResult;
    }
}
