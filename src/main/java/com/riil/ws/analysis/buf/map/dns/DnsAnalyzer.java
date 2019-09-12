package com.riil.ws.analysis.buf.map.dns;

import com.riil.ws.analysis.buf.map.FrameBean;
import com.riil.ws.analysis.buf.map.FrameConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DnsAnalyzer {
    private final Logger LOGGER = LoggerFactory.getLogger(DnsAnalyzer.class);

    public void analysis(DnsSession dnsSession) throws Exception {
        List<FrameBean> frames = dnsSession.getFrames();
        for (FrameBean frame : frames) {
            // 比如：icmp的type=3 code=3，意思是 Port unreachable
            if (frame.containsIcmp()) {
                continue;
            }
            if (frame.isDnsQry()) {
                if (dnsSession.getQryTime() == null) {
                    dnsSession.setQryTime(frame.getTimestamp());
                }
                if (dnsSession.getClientIp() == null) {
                    dnsSession.setClientIp(frame.getSrcIp());
                    dnsSession.setServerIp(frame.getDstIp());
                }
            } else { // 获取到第一个响应的时候，结束分析。因为之前的请求有重发的，后续的响应是对重发的请求的响应。
                // 可能没有抓到请求包
                if (dnsSession.getQryTime() != null) {
                    frame.setDnsReplyDelay(frame.getTimestamp() - dnsSession.getQryTime());
                }
                if (dnsSession.getClientIp() == null) {
                    frame.setClientIp(frame.getDstIp());
                    frame.setServerIp(frame.getSrcIp());
                } else {
                    frame.setClientIp(dnsSession.getClientIp());
                    frame.setServerIp(dnsSession.getServerIp());
                }
                if (frame.getDnsFlagsRcode().equals(FrameConstant.DNS_FLAGS_RCODE_NO_ERROR)) {
                    // 虽然DNS服务器返回了应答，但是没有返回ip，也是无响应
                    if (frame.getDnsAnswerIp() == null) {
                        frame.setDnsServerRespNoIp();
                        frame.setDnsReplyDelay(null);
                    } else {
                        frame.setDnsQrySuccess();
                    }
                } else {
                    frame.setDnsErrorAnswer();
                }
                dnsSession.setHasResult(true);

                break;
            }
        }

        // 对frame循环结束，DNS没有结果，则是无响应
        if (!dnsSession.isHasResult()) {
            FrameBean lastFrame = frames.get(frames.size() - 1);
            lastFrame.setClientIp(dnsSession.getClientIp());
            lastFrame.setServerIp(dnsSession.getServerIp());
            lastFrame.setDnsNoResponse();
            lastFrame.setDnsReplyDelay(null);
        }
    }
}
