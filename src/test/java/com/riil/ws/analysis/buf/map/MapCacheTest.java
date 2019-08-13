package com.riil.ws.analysis.buf.map;

import com.riil.ws.analysis.common.IpV4Util;
import org.junit.Assert;
import org.junit.Test;
import sun.net.util.IPAddressUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MapCacheTest {
    private Random r = new Random();

    @Test
    public void putString() throws InterruptedException {

        Map<Integer, TestBeanStr> map = new HashMap<>();

        for (int i = 0; i < 1000000; i++) {
            TestBeanStr b = new TestBeanStr();

            b.setIp(IpV4Util.ipInt2Str(r.nextInt()));
            map.put(i, b);
        }

        Thread.sleep(60000);
    }

    @Test
    public void putInt() throws InterruptedException {
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
        int ip = IpV4Util.ipStr2Int(address);
        String ipStr = IpV4Util.ipInt2Str(ip);

        Assert.assertTrue(address.equals(ipStr));
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