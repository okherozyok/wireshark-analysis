package com.riil.ws.analysis.buf.map;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author GlobalZ
 *
 */
@Service
public class TcpAnalyzer {
	private final Logger LOGGER = LoggerFactory.getLogger(TcpAnalyzer.class);

	public void analysis(TcpStream tcpStream) throws Exception {

		List<Frame> frames = tcpStream.getFrames();
		for (Frame frame : frames) {
			realTimeCreateConn(frame, tcpStream);
		}

		endCreateConn(tcpStream);

		transportPhase(tcpStream);
	}

	/**
	 * 实时建连阶段分析。从有syn数据开始分析，能够分析出成功和rst的情况。
	 * 此分析过程不能判断建连无响应的情况，建连无响应的状态是在一个tcpStream结束时判断的。
	 * 建连成功状态会在分析过程中设置或者清楚：当分析出syn+ack的ack时，设置建连成功状态，当遇到syn重传或者syn+ack重传时，清除建连成功状态。
	 * 该方法会在一个tcpStream的过程中一直被调用，因为目前还不能准确判断出数据传输是什么时候开始。
	 * 业务定义（客户端建连时延、服务端建连时延）与ws的ack_frame不同。有些场景，不能使用ack_frame直接匹配syn+ack，需要将syn+ack的number存储起来，查找是否包含ack_frame
	 * 
	 * @param frame
	 * @param tcpStream
	 */
	private void realTimeCreateConn(Frame frame, TcpStream tcpStream) {
		if (frame.isTcpConnectionSyn()) { // syn包
			tcpStream.setHasSyn(true);

			// 只记录第一个syn包的时间
			if (tcpStream.getSynTimeStamp() == null) {
				tcpStream.setSynTimeStamp(frame.getTimeStamp());
				tcpStream.setClientIp(frame.getSrcIp());
				tcpStream.setServerIp(frame.getDstIp());
			}

			// 可能发生了syn重传，清除建连成功的标志
			clearTcpConnSuccess(tcpStream);
		} else if (frame.isTcpConnectionSack()) { // syn+ack包
			if (tcpStream.getClientIp() == null && tcpStream.getServerIp() == null) {
				// 丢失syn包的时候，纠正下客户端和服务端ip
				tcpStream.setClientIp(frame.getDstIp());
				tcpStream.setServerIp(frame.getSrcIp());
			}
			tcpStream.addSackFrameNumber(frame.getFrameNumber());
			tcpStream.setSackTimeStamp(frame.getTimeStamp());

			// 可能发生了syn+ack 重传，清除建连成功的标志
			clearTcpConnSuccess(tcpStream);
		} else if (frame.isTcpConnectionRst()) { // rst包
			if (tcpStream.getTcpConnectionSuccess() == null) {
				if (!tcpStream.hasSyn()) {
					LOGGER.warn("TcpStream: " + tcpStream.getTcpStreamNumber() + ", ifHasSyn=" + tcpStream.hasSyn()
							+ ", Can't judge client or server connection rst");
				} else {
					// RST建连失败
					tcpStream.setTcpConnectionSuccess(Boolean.FALSE);
					frame.setClientIp(tcpStream.getClientIp());
					frame.setServerIp(tcpStream.getServerIp());
					if (frame.getSrcIp().equals(tcpStream.getClientIp())) { // 客户端建连RST
						frame.setTcpClientConnectionRst();
					} else if (frame.getSrcIp().equals(tcpStream.getServerIp())) { // 服务端建连RST
						frame.setTcpServerConnectionRst();
					}
				}
			}
		} else if (frame.getTcpAckFrameNumber() != null // syn+ack包的ack包
				&& tcpStream.hasSackFrameNumber(frame.getTcpAckFrameNumber())) {
			if (tcpStream.hasSyn()) {
				markTcpConnSuccess(tcpStream, frame);
			}
		}
	}

	private void markTcpConnSuccess(TcpStream tcpStream, Frame frame) {
		tcpStream.setTcpConnAckTimeStamp(frame.getTimeStamp());
		tcpStream.setTcpConnAckFrameNumber(frame.getFrameNumber());

		// 建连成功，如果发生syn或者syn+ack的重传，这个标志会被清除
		frame.setTcpConnectionSuccess();
		frame.setClientIp(tcpStream.getClientIp());
		frame.setServerIp(tcpStream.getServerIp());
		tcpStream.setTcpConnectionSuccess(Boolean.TRUE);
	}

	/**
	 * 遇到syn和syn+ack时被调用，如果已经是建连成功的，清除建连成功状态。
	 * 
	 * @param tcpStream
	 */
	private void clearTcpConnSuccess(TcpStream tcpStream) {
		if (Boolean.TRUE.equals(tcpStream.getTcpConnectionSuccess())) {
			Frame frame = MapCache.getFrame(tcpStream.getTcpConnAckFrameNumber());
			frame.delClientIp();
			frame.delServerIp();
			frame.delTcpConnectionSuccess();
			tcpStream.setTcpConnectionSuccess(null);
			tcpStream.setTcpConnAckFrameNumber(null);
			tcpStream.setTcpConnAckTimeStamp(null);
		}
	}

	/**
	 * 结束时建连状态分析，判断建连无响应的情况，判断一个tcpStream是否有syn包。
	 * <p>
	 * 结束时建连时延分析，判断出客户端建连时延、服务端建连时延、总建连时延
	 * 
	 * @param tcpStream
	 */
	private void endCreateConn(TcpStream tcpStream) {

		if (tcpStream.getTcpConnectionSuccess() == null) {
			if (!tcpStream.hasSyn()) {
				LOGGER.warn("TcpStream: " + tcpStream.getTcpStreamNumber() + ", ifHasSyn=" + tcpStream.hasSyn()
						+ ", can't judge client or server no response.");
			} else {
				List<Frame> frames = tcpStream.getFrames();
				Frame lastFrame = frames.get(frames.size() - 1);
				lastFrame.setClientIp(tcpStream.getClientIp());
				lastFrame.setServerIp(tcpStream.getServerIp());
				if (tcpStream.getSynTimeStamp() != null && tcpStream.getSackTimeStamp() != null) {
					lastFrame.setTcpClientConnectionNoResp();
				} else if (tcpStream.getSynTimeStamp() != null) {
					lastFrame.setTcpServerConnectionNoResp();
				}
			}
		} else if (Boolean.TRUE.equals(tcpStream.getTcpConnectionSuccess())) { // 建连成功时，计算建连时延
			long clientConnDelay = tcpStream.getSackTimeStamp() - tcpStream.getSynTimeStamp();
			long serverConnDelay = tcpStream.getTcpConnAckTimeStamp() - tcpStream.getSackTimeStamp();
			int connAckFrameNumver = tcpStream.getTcpConnAckFrameNumber();
			MapCache.getFrame(tcpStream.getLastSackFrameNumber())
					.setTcpClientConnectionDelay(String.valueOf(clientConnDelay));
			MapCache.getFrame(connAckFrameNumver).setTcpServerConnectionDelay(String.valueOf(serverConnDelay));
			MapCache.getFrame(connAckFrameNumver)
					.setTcpConnectionDelay(String.valueOf(clientConnDelay + serverConnDelay));
		}
	}

	/**
	 * 传输阶段，只计算建连成功的。
	 * 
	 * @param tcpStream
	 */
	private void transportPhase(TcpStream tcpStream) {
		if (tcpStream.getTcpConnectionSuccess() == null || tcpStream.getTcpConnectionSuccess().equals(Boolean.FALSE)) {
			return;
		}

		List<Frame> frames = tcpStream.getFrames();
		for (Frame frame : frames) {
			transportPhaseRTT(tcpStream, frame);
		}
	}

	private void transportPhaseRTT(TcpStream tcpStream, Frame frame) {
		// 不计算三次握手的
		if (frame.isTcpConnectionSyn() || frame.isTcpConnectionSack()) {
			return;
		}
		if (frame.getTcpAckFrameNumber() != null && !tcpStream.hasSackFrameNumber(frame.getTcpAckFrameNumber())) { // 不计算三次握手的
			// 不计算tcp 长度 大于 0 的
			if (frame.getTcpLen() > 0) {
				return;
			}

			Long rtt = frame.getTcpAnalysisAckRtt();
			if (frame.getSrcIp().equals(tcpStream.getServerIp())) {
				frame.setTcpUpRTT(String.valueOf(rtt));
			} else if (frame.getSrcIp().equals(tcpStream.getClientIp())) {
				frame.setTcpDownRTT(String.valueOf(rtt));
			} else {
				LOGGER.error("Tcp stream: " + tcpStream.getTcpStreamNumber()
						+ ", Can't judge server ip or client ip, frame src_ip: " + frame.getSrcIp());
			}
		}
	}
}
