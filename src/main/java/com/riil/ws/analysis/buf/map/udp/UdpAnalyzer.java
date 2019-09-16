package com.riil.ws.analysis.buf.map.udp;

import com.riil.ws.analysis.buf.map.FrameBean;
import com.riil.ws.analysis.buf.map.FrameConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UdpAnalyzer {
    private final Logger LOGGER = LoggerFactory.getLogger(UdpAnalyzer.class);

    public void analysis(UdpStream udpStream) throws Exception {
        List<FrameBean> frames = udpStream.getFrames();
        for (FrameBean frame : frames) {
            // 比如：icmp的type=3 code=3，意思是 Port unreachable
            if (frame.containsIcmp()) {
                continue;
            }
            realTimeDns(frame, udpStream);
        }

        endDns(udpStream);
    }

    private void realTimeDns(FrameBean frame, UdpStream udpStream) {
        Boolean dnsQryHost = frame.getDnsQryHost();
        if (!Boolean.TRUE.equals(dnsQryHost)) {
            return;
        }

        if (udpStream.isHasDnsResult() == null) {
            udpStream.setHasDnsResult(false);
        } else if (Boolean.TRUE.equals(udpStream.isHasDnsResult())) {
            return;
        }

        if (frame.isDnsQry()) {
            if (udpStream.getQryTime() == null) {
                udpStream.setQryTime(frame.getTimestamp());
            }
            if (udpStream.getClientIp() == null) {
                udpStream.setClientIp(frame.getSrcIp());
                udpStream.setServerIp(frame.getDstIp());
            }
        } else { // 获取到第一个响应的时候，结束分析。因为之前的请求有重发的，后续的响应是对重发的请求的响应。
            // 可能没有抓到请求包
            if (udpStream.getQryTime() != null) {
                frame.setDnsReplyDelay(frame.getTimestamp() - udpStream.getQryTime());
            }
            if (udpStream.getClientIp() == null) {
                frame.setClientIp(frame.getDstIp());
                frame.setServerIp(frame.getSrcIp());
            } else {
                frame.setClientIp(udpStream.getClientIp());
                frame.setServerIp(udpStream.getServerIp());
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
            udpStream.setHasDnsResult(true);
        }
    }

    private void endDns(UdpStream udpStream) {
        // 对frame循环结束，DNS没有结果，则是无响应
        if (Boolean.FALSE.equals(udpStream.isHasDnsResult())) {
            List<FrameBean> frames = udpStream.getFrames();
            FrameBean lastFrame = frames.get(frames.size() - 1);
            lastFrame.setClientIp(udpStream.getClientIp());
            lastFrame.setServerIp(udpStream.getServerIp());
            lastFrame.setDnsNoResponse();
            lastFrame.setDnsReplyDelay(null);
        }
    }
}
