package com.riil.ws.analysis.buf.map;

import java.util.HashMap;
import java.util.Map;

public final class MapCache {
	private static final int INITIAL_CAPACITY = 100000;

	private static final Map<Integer, Frame> FRAME_MAP = new HashMap<>(INITIAL_CAPACITY);
	private static final Map<Integer, TcpStream> TCP_STREAM_MAP = new HashMap<>();

	public static int getInitCapacity() {
		return INITIAL_CAPACITY;
	}

	public static void putFrame(int frameNumber, Frame frame) {
		FRAME_MAP.put(frameNumber, frame);
	}

	public static Frame getFrame(Integer frameNumber) {
		return FRAME_MAP.get(frameNumber);
	}

	public static Map<Integer, Frame> getFrameMap() {
		return FRAME_MAP;
	}

	public static void putTcpStream(TcpStream tcpStream) {
		TCP_STREAM_MAP.put(tcpStream.getTcpStreamNumber(), tcpStream);
	}

	public static TcpStream getTcpStream(Integer tcpStreamNumber) {
		return TCP_STREAM_MAP.get(tcpStreamNumber);
	}

	public static Map<Integer, TcpStream> getTcpStreamMap() {
		return TCP_STREAM_MAP;
	}
}
