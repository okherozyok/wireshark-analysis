package com.riil.ws.analysis.buf.map.dns;

import com.riil.ws.analysis.buf.map.FrameBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DnsAnalyzer {
    private final Logger LOGGER = LoggerFactory.getLogger(DnsAnalyzer.class);

    public void analysis(DnsSession dnsSession) throws Exception {
        List<FrameBean> frames = dnsSession.getFrames();
        for(FrameBean frame : frames) {
            if(frame.isDnsQry()) {
                dnsSession.setQryTime(frame.getTimestamp());
            } else { // 获取到第一个响应的时候，结束分析

                break;
            }
        }
    }
}
