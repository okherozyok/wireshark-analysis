package com.riil.ws.analysis.buf.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TcpStream {
	private Integer tcpStreamNumber;

	private String clientIp = null;
	private String serverIp = null;
	private Integer dstPort = null;
	private Long synTimeStamp = null;
	private Long sackTimeStamp = null;
	private Set<Integer> sackFrameNumberSet = new LinkedHashSet<Integer>();
	private Long tcpConnAckTimeStamp = null;
	private Integer tcpConnAckFrameNumber = null;
	private Boolean tcpConnectionSuccess = null;
	private boolean hasSyn = false;

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

	public Integer getDstPort() {
		return dstPort;
	}

	public void setDstPort(Integer dstPort) {
		this.dstPort = dstPort;
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

	public Boolean hasSyn() {
		return hasSyn;
	}

	public void setHasSyn(boolean hasSyn) {
		this.hasSyn = hasSyn;
	}

	public void append(FrameBean frame) {
		frames.add(frame);
	}

	public List<FrameBean> getFrames() {
		return frames;
	}
}
