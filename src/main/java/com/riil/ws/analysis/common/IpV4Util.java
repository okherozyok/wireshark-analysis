package com.riil.ws.analysis.common;

import sun.net.util.IPAddressUtil;

public final class IpV4Util {
    public static int ipStr2Int(String address) {
        byte[] bytes = IPAddressUtil.textToNumericFormatV4(address);
        int ip = (0xff000000 & (bytes[0] << 24)) |
                (0x00ff0000 & (bytes[1] << 16)) |
                (0x0000ff00 & (bytes[2] << 8)) |
                (0x000000ff & bytes[3]);

        return ip;
    }

    public static String ipInt2Str(int address) {
        byte[] b = new byte[4];
        b[0] = (byte) ((address & 0xff000000) >> 24);
        b[1] = (byte) ((address & 0x00ff0000) >> 16);
        b[2] = (byte) ((address & 0x0000ff00) >> 8);
        b[3] = (byte) (address & 0x000000ff);

        StringBuilder sb = new StringBuilder();
        return sb.append(Byte.toUnsignedInt(b[0])).append(".").append(Byte.toUnsignedInt(b[1])).append(".")
                .append(Byte.toUnsignedInt(b[2])).append(".").append(Byte.toUnsignedInt(b[3])).toString();
    }
}
