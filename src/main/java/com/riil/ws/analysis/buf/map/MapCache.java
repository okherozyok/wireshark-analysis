package com.riil.ws.analysis.buf.map;

import com.riil.ws.analysis.buf.map.dns.DnsSession;
import com.riil.ws.analysis.buf.map.tcp.TcpStream;

import java.util.HashMap;
import java.util.Map;

public final class MapCache {
    private static final int INITIAL_CAPACITY = 100000;

    private static final Map<Integer, FrameBean> FRAME_BEAN_MAP = new HashMap<>(INITIAL_CAPACITY);
    private static final Map<Integer, TcpStream> TCP_STREAM_MAP = new HashMap<>();
    private static final Map<Integer, DnsSession> DNS_SESSION_MAP = new HashMap<>();

    // ip+port key，timestamp key
    private static final Map<Long, Map<Long, ConcurrentConnBean>> CONCURRENT_CONN_CACHE = new HashMap<>();

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

    public static void putDnsSession(DnsSession dnsSession) {
        DNS_SESSION_MAP.put(dnsSession.getDnsId(), dnsSession);
    }

    public static DnsSession getDnsSession(int dnsId) {
        return DNS_SESSION_MAP.get(dnsId);
    }

    public static Map<Integer, DnsSession> getDnsSessionMap() {
        return DNS_SESSION_MAP;
    }
}
