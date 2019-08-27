package com.riil.ws.analysis.buf.map;

import com.alibaba.fastjson.annotation.JSONField;
import com.riil.ws.analysis.common.IpV4Util;

import java.util.ArrayList;
import java.util.List;

public class ConcurrentReqBean {
    @JSONField(serialize = false)
    private String index;

    private long timestamp;

    @JSONField(serialize = false)
    private int serverIpInt;

    @JSONField(name = FrameConstant.TCP_DSTPORT)
    private int tcpDstPort;

    @JSONField(name = FrameConstant.REQ_COUNT)
    private int reqCount = 0;

    @JSONField(name = FrameConstant.FRAME_NUMBER)
    private List<Integer> frameNumber = new ArrayList<>();

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

    @JSONField(name = FrameConstant.SERVER_IP)
    public String getServerIp() {
        return IpV4Util.ipInt2Str(serverIpInt);
    }

    public void setServerIp(String serverIp) {
        this.serverIpInt = IpV4Util.ipStr2Int(serverIp);
    }

    public int getTcpDstPort() {
        return tcpDstPort;
    }

    public void setTcpDstPort(int tcpDstPort) {
        this.tcpDstPort = tcpDstPort;
    }

    public int getReqCount() {
        return reqCount;
    }

    public void addReqCount() {
        reqCount++;
    }

    public List<Integer> getFrameNumber() {
        return frameNumber;
    }

    public void addFrameNumber(int frameNumber) {
        this.frameNumber.add(frameNumber);
    }
}
