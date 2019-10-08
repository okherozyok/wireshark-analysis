package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.FrameBean;

import java.util.*;

public class TcpStream {
    private Integer tcpStreamNumber;

    private String clientIp = null;
    private String serverIp = null;

    // 根据第一条frame判断客户端、服务端。
    // 不使用clientIp、serverIp的原因是如果没有syn和syn+ack，和http的request标记可能相反
    private String clientIpByFirst = null;
    private String serverIpByFirst = null;
    private Integer clientPortByFirst = null;
    private Integer serverPortByFirst = null;

    private String onlineUserIp = null;

    private Integer clientPort = null;
    private Integer serverPort = null;
    private Long synTimeStamp = null;
    private Long sackTimeStamp = null;
    private Set<Integer> sackFrameNumberSet = new LinkedHashSet<Integer>();
    private Long tcpConnAckTimeStamp = null;
    private Integer tcpConnAckFrameNumber = null;
    private Boolean tcpConnectionSuccess = null;
    private Map<Integer, List<Integer>> clientDupAckMap = new HashMap<>();
    private Map<Integer, List<Integer>> serverDupAckMap = new HashMap<>();
    private int clientZeroWinNum = 0;
    private int serverZeroWinNum = 0;
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

    public String getOnlineUserIp() {
        return onlineUserIp;
    }

    public void setOnlineUserIp(String onlineUserIp) {
        this.onlineUserIp = onlineUserIp;
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

    public void addClientZeroWinNum() {
        clientZeroWinNum ++;
    }

    public int getClientZeroWinNum() {
        return clientZeroWinNum;
    }

    public void addServerZeroWinNum() {
        serverZeroWinNum ++;
    }

    public int getServerZeroWinNum() {
        return serverZeroWinNum;
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
