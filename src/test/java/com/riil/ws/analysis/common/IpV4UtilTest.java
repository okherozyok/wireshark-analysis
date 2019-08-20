package com.riil.ws.analysis.common;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StringUtils;

import static org.junit.Assert.*;

public class IpV4UtilTest {

    @Test
    public void ipStr2Int() {
        String address = "255.127.128.0";
        int ip = IpV4Util.ipStr2Int(address);
        String ipStr = IpV4Util.ipInt2Str(ip);

        Assert.assertTrue(address.equals(ipStr));
    }

    @Test
    public void ipInt2Str() {
        String id="0000105c";
        int i = 0x0000105c;
        System.out.println(Integer.parseInt(id, 16));
        Assert.assertTrue(Integer.parseInt(id, 16) == i);

        System.out.println(Boolean.FALSE.equals(null));
    }
}