package com.riil.ws.analysis.common;

public final class IpPortUtil {
    public static long ipPortKey(String ip, int port) {
        int ipInt = IpV4Util.ipStr2Int(ip);
        long l = (0xffffffff00000000L & (Integer.toUnsignedLong(ipInt) << 32)) | port;
        return l;
    }

}
