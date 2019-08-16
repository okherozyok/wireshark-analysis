package com.riil.ws.analysis.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class IpPortUtilTest {

    @Test
    public void ipPortKey() {
        String ip = "255.127.128.255";
        int ipInt = IpV4Util.ipStr2Int(ip);
        String ipI = Integer.toBinaryString(ipInt);
        System.out.println(ipI);
        long ipLong = Integer.toUnsignedLong(ipInt);
        String ipL = Long.toBinaryString(ipLong);
        System.out.println(ipL);

        ipLong = 0xffffffff00000000L & (ipLong << 32);
        ipL = Long.toBinaryString(ipLong);
        System.out.println(ipL);

        int port = 65536;
        System.out.println(Integer.toBinaryString(port));
        long l = IpPortUtil.ipPortKey(ip, 65536);

        System.out.println(Long.toBinaryString(l));
    }
}