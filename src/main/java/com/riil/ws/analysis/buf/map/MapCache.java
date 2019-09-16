package com.riil.ws.analysis.buf.map;

import com.riil.ws.analysis.buf.map.udp.UdpStream;
import com.riil.ws.analysis.buf.map.tcp.TcpStream;

import java.util.HashMap;
import java.util.Map;

public final class MapCache {
    private static final int INITIAL_CAPACITY = 100000;

    private static final Map<Integer, FrameBean> FRAME_BEAN_MAP = new HashMap<>(INITIAL_CAPACITY);
    private static final Map<Integer, TcpStream> TCP_STREAM_MAP = new HashMap<>();
    private static final Map<Integer, UdpStream> UDP_STREAM_MAP = new HashMap<>();

    // ip+port key，timestamp key
    private static final Map<Long, Map<Long, ConcurrentConnBean>> CONCURRENT_CONN_CACHE = new HashMap<>();
    private static final Map<Long, Map<Long, ConcurrentReqBean>> CONCURRENT_REQ_CACHE = new HashMap<>();

    public static int getInitCapacity() {
        return INITIAL_CAPACITY;
    }

    public static void putFrame(int frameNumber, FrameBean frame) {
        FRAME_BEAN_MAP.put(frameNumber, frame);
    }

    public static FrameBean getFrame(Integer frameNumber) {
        return FRAME_BEAN_MAP.get(frameNumber);
    }

    public static Map<Integer, FrameBean> getFrameMap() {
        return FRAME_BEAN_MAP;
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

    public static Map<Long, Map<Long, ConcurrentConnBean>> getConcurrentConnCache() {
        return CONCURRENT_CONN_CACHE;
    }

    public static Map<Long, Map<Long, ConcurrentReqBean>> getConcurrentReqCache() {
        return CONCURRENT_REQ_CACHE;
    }

    public static void putUdpStream(UdpStream udpStream) {
        UDP_STREAM_MAP.put(udpStream.getUdpStreamNumber(), udpStream);
    }

    public static UdpStream getUdpStream(int udpStreamNumber) {
        return UDP_STREAM_MAP.get(udpStreamNumber);
    }

    public static Map<Integer, UdpStream> getUdpStreamMap() {
        return UDP_STREAM_MAP;
    }
}
