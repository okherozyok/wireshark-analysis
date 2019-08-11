package com.riil.ws.analysis.buf.map;

import org.junit.Assert;
import org.junit.Test;
import sun.net.util.IPAddressUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MapCacheTest {
    private Random r = new Random();

    @Test
    public void putFrame() throws InterruptedException {

        Map<Integer, TestBeanStr> map = new HashMap<>();

        for (int i = 0; i < 1000000; i++) {
            TestBeanStr b = new TestBeanStr();

            b.setIp(ipInt2Str(r.nextInt()));
            map.put(i, b);
        }

        Thread.sleep(60000);
    }

    @Test
    public void putFrameBean() throws InterruptedException {
        Map<Integer, TestBeanInt> map = new HashMap<>();
        for (int i = 0; i < 1000000; i++) {
            TestBeanInt b = new TestBeanInt();
            b.setIp(r.nextInt());
            map.put(i, b);
        }

        Thread.sleep(60000);
    }

    @Test
    public void ipConvert() {
        String address = "255.127.128.0";
        int ip = ipStr2Int(address);
        String ipStr = ipInt2Str(ip);

        Assert.assertTrue(address.equals(ipStr));
    }

    private int ipStr2Int(String address) {
        byte[] bytes = IPAddressUtil.textToNumericFormatV4(address);
        int ip = (0xff000000 & (bytes[0] << 24)) |
                (0x00ff0000 & (bytes[1] << 16)) |
                (0x0000ff00 & (bytes[2] << 8)) |
                (0x000000ff & bytes[3]);

        return ip;
    }

    private String ipInt2Str(int address) {
        byte[] b = new byte[4];
        b[0] = (byte) ((address & 0xff000000) >> 24);
        b[1] = (byte) ((address & 0x00ff0000) >> 16);
        b[2] = (byte) ((address & 0x0000ff00) >> 8);
        b[3] = (byte) (address & 0x000000ff);

        StringBuilder sb = new StringBuilder();
        return sb.append(Byte.toUnsignedInt(b[0])).append(".").append(Byte.toUnsignedInt(b[1])).append(".")
                .append(Byte.toUnsignedInt(b[2])).append(".").append(Byte.toUnsignedInt(b[3])).toString();
    }

    public static class TestBeanStr {
        private String ip;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }

    public static class TestBeanInt {
        private int ip;

        public int getIp() {
            return ip;
        }

        public void setIp(int ip) {
            this.ip = ip;
        }
    }
}