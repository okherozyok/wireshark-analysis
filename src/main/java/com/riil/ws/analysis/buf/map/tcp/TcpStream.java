package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.FrameBean;

import java.util.*;

public class TcpStream {
    private Integer tcpStreamNumber;

    // 根据第一条frame判断客户端、服务端。
    // 不使用clientIp、serverIp的原因是如果没有syn和syn+ack，和http的request标记可能相反
    private String clientIp = null;
    private String serverIp = null;
    private Integer clientPort = null;
    private Integer serverPort = null;

    private long wholeTimePointStartTime = 0;
    private String onlineUserIp = null;

    private Long synTimeStamp = null;
    private Long sackTimeStamp = null;
    private Set<Integer> sackFrameNumberSet = new LinkedHashSet<>();
    private Long tcpConnAckTimeStamp = null;
    private Integer tcpConnAckFrameNumber = null;
    private Boolean tcpConnectionSuccess = null;
    private Map<Integer, List<Integer>> clientDupAckMap = new HashMap<>();
    private Map<Integer, List<Integer>> serverDupAckMap = new HashMap<>();
    private long longConnMetricStartTime = 0;
    private long clientZeroWinStartTime = 0;
    private long serverZeroWinStartTime = 0;
    private int clientZeroWinNum = 0;
    private int serverZeroWinNum = 0;
    private int lessEq64 = 0;
    private int between65_511 = 0;
    private int between512_1023 = 0;
    private int greaterEq1024 = 0;
    private Integer clientFinFrame = null;
    private Integer serverFinFrame = null;
    private Integer clientRstFrame = null;
    private Integer serverRstFrame = null;
    private boolean hasSyn = false;
    private Long onlineUserTimeStamp = null;

    private List<FrameBean> frames;

    public TcpStream(Integer tcpStreamNumber) {
        this.tcpStreamNumber = tcpStreamNumber;
        frames = new ArrayList<>();
    }

    public Integer getTcpStreamNumber() {
        return tcpStreamNumber;
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

    public long getWholeTimePointStartTime() {
        return wholeTimePointStartTime;
    }

    public void setWholeTimePointStartTime(long wholeTimePointStartTime) {
        this.wholeTimePointStartTime = wholeTimePointStartTime;
    }

    public String getOnlineUserIp() {
        return onlineUserIp;
    }

    public void setOnlineUserIp(String onlineUserIp) {
        this.onlineUserIp = onlineUserIp;
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

    public Long getSynTimeStamp() {
        return synTimeStamp;
    }

    public void setSynTimeStamp(Long tcpConnSynTimeStamp) {
        this.synTimeStamp = tcpConnSynTimeStamp;
    }

    public Long getSackTimeStamp() {
        return sackTimeStamp;
    }

    public void setSackTimeStamp(Long tcpConnSackTimeStamp) {
        this.sackTimeStamp = tcpConnSackTimeStamp;
    }

    public Long getTcpConnAckTimeStamp() {
        return tcpConnAckTimeStamp;
    }

    public void setTcpConnAckTimeStamp(Long tcpConnAckTimeStamp) {
        this.tcpConnAckTimeStamp = tcpConnAckTimeStamp;
    }

    public Integer getTcpConnAckFrameNumber() {
        return tcpConnAckFrameNumber;
    }

    public void setTcpConnAckFrameNumber(Integer tcpConnAckFrameNumber) {
        this.tcpConnAckFrameNumber = tcpConnAckFrameNumber;
    }

    public boolean hasSackFrameNumber(Integer tcpConnSackFrameNumber) {
        return this.sackFrameNumberSet.contains(tcpConnSackFrameNumber);
    }

    public void addSackFrameNumber(Integer tcpConnSackFrameNumber) {
        this.sackFrameNumberSet.add(tcpConnSackFrameNumber);
    }

    public Integer getLastSackFrameNumber() {
        Integer last = null;
        Iterator<Integer> iterator = sackFrameNumberSet.iterator();
        while (iterator.hasNext()) {
            last = iterator.next();
        }

        return last;
    }

    public Boolean getTcpConnectionSuccess() {
        return tcpConnectionSuccess;
    }

    public void setTcpConnectionSuccess(Boolean tcpConnectionSuccess) {
        this.tcpConnectionSuccess = tcpConnectionSuccess;
    }

    public void putClientDupAck(Integer dupAckFrame, Integer dupAckNum) {
        putDupAck(clientDupAckMap, dupAckFrame, dupAckNum);
    }

    public int getClientDupAckNum() {
        return getDupAck(clientDupAckMap);
    }

    public void putServerDupAck(Integer dupAckFrame, Integer dupAckNum) {
        putDupAck(serverDupAckMap, dupAckFrame, dupAckNum);
    }

    public int getServerDupAckNum() {
        return getDupAck(serverDupAckMap);
    }

    public long getLongConnMetricStartTime() {
        return longConnMetricStartTime;
    }

    public void setLongConnMetricStartTime(long longConnMetricStartTime) {
        this.longConnMetricStartTime = longConnMetricStartTime;
    }

    public long getClientZeroWinStartTime() {
        return clientZeroWinStartTime;
    }

    public void setClientZeroWinStartTime(long clientZeroWinStartTime) {
        this.clientZeroWinStartTime = clientZeroWinStartTime;
    }

    public long getServerZeroWinStartTime() {
        return serverZeroWinStartTime;
    }

    public void setServerZeroWinStartTime(long serverZeroWinStartTime) {
        this.serverZeroWinStartTime = serverZeroWinStartTime;
    }

    public void clearClientZeroWinNum() {
        clientZeroWinNum = 0;
    }

    public void addClientZeroWinNum() {
        clientZeroWinNum++;
    }

    public int getClientZeroWinNum() {
        return clientZeroWinNum;
    }

    public void clearServerZeroWinNum() {
        serverZeroWinNum = 0;
    }

    public void addServerZeroWinNum() {
        serverZeroWinNum++;
    }

    public int getServerZeroWinNum() {
        return serverZeroWinNum;
    }

    public void addLessEq64() {
        lessEq64++;
    }

    public void clearLessEq64() {
        lessEq64 = 0;
    }

    public int getLessEq64() {
        return lessEq64;
    }

    public void addBetween65_511() {
        between65_511++;
    }

    public void clearBetween65_511() {
        between65_511 = 0;
    }

    public int getBetween65_511() {
        return between65_511;
    }

    public void addBetween512_1023() {
        between512_1023++;
    }

    public void clearBetween512_1023() {
        between512_1023 = 0;
    }

    public int getBetween512_1023() {
        return between512_1023;
    }

    public void addGreaterEq1024() {
        greaterEq1024++;
    }

    public void clearGreaterEq1024() {
        greaterEq1024 = 0;
    }

    public int getGreaterEq1024() {
        return greaterEq1024;
    }

    public Integer getClientFinFrame() {
        return clientFinFrame;
    }

    public void setClientFinFrame(Integer clientFinFrame) {
        this.clientFinFrame = clientFinFrame;
    }

    public Integer getServerFinFrame() {
        return serverFinFrame;
    }

    public void setServerFinFrame(Integer serverFinFrame) {
        this.serverFinFrame = serverFinFrame;
    }

    public Integer getClientRstFrame() {
        return clientRstFrame;
    }

    public void setClientRstFrame(Integer clientRstFrame) {
        this.clientRstFrame = clientRstFrame;
    }

    public Integer getServerRstFrame() {
        return serverRstFrame;
    }

    public void setServerRstFrame(Integer serverRstFrame) {
        this.serverRstFrame = serverRstFrame;
    }

    public Boolean hasSyn() {
        return hasSyn;
    }

    public void setHasSyn(boolean hasSyn) {
        this.hasSyn = hasSyn;
    }

    public Long getOnlineUserTimeStamp() {
        return onlineUserTimeStamp;
    }

    public void setOnlineUserTimeStamp(Long onlineUserTimeStamp) {
        this.onlineUserTimeStamp = onlineUserTimeStamp;
    }

    public void append(FrameBean frame) {
        frames.add(frame);
    }

    public List<FrameBean> getFrames() {
        return frames;
    }

    public void putDupAck(Map<Integer, List<Integer>> map, Integer dupAckFrame, Integer dupAckNum) {
        List<Integer> nums = map.get(dupAckFrame);
        if (nums == null) {
            nums = new ArrayList<>();
            map.put(dupAckFrame, nums);
        }

        nums.add(dupAckNum);
    }

    public int getDupAck(Map<Integer, List<Integer>> map) {
        int count = 0;
        Set<Integer> keySet = map.keySet();
        for (Integer dupAckFrame : keySet) {
            if (map.get(dupAckFrame).size() >= 3) {
                count++;
            }
        }

        return count;
    }
}
