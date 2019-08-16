package com.riil.ws.analysis.common;

import org.junit.Assert;
import org.junit.Test;

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
    }
}