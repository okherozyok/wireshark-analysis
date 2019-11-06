package com.riil.ws.analysis.buf.map.udp;

import com.riil.ws.analysis.buf.map.FrameBean;
import com.riil.ws.analysis.buf.map.FrameConstant;
import com.riil.ws.analysis.buf.map.MapCache;
import com.riil.ws.analysis.common.Contants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UdpAnalyzer {
    private final Logger LOGGER = LoggerFactory.getLogger(UdpAnalyzer.class);

    public void analysis(UdpStream udpStream) throws Exception {
        List<FrameBean> frames = udpStream.getFrames();
        for (FrameBean frame : frames) {
            markIpDirectionByFirst(frame, udpStream);

            // 比如：icmp的type=3 code=3，意思是 Port unreachable
            if (frame.containsIcmp()) {
                continue;
            }
            realTimeDns(frame, udpStream);
        }

        endDns(udpStream);
    }

    private void markIpDirectionByFirst(FrameBean frame, UdpStream udpStream) {
        // TODO NPV没有处理udp.stream中的icmp流量，
        if (frame.containsIcmp()) {
            return;
        }

        if (!StringUtils.isEmpty(udpStream.getClientIp())) {
            frame.setClientIp(udpStream.getClientIp());
            frame.setServerIp(udpStream.getServerIp());
            frame.setClientPort(udpStream.getClientPort());
            frame.setServerPort(udpStream.getServerPort());

            setUdpIp4IpFragment(frame, udpStream);
            return;
        }

        if (frame.getUdpSrcPort().equals(Contants.DNS_PORT)) {
            udpStream.setClientIp(frame.getDstIp());
            udpStream.setServerIp(frame.getSrcIp());
            udpStream.setClientPort(frame.getUdpDstPort());
            udpStream.setServerPort(frame.getUdpSrcPort());
        } else { // 以第一条的srcIp作为客户端IP，与NPV保持一致
            udpStream.setClientIp(frame.getSrcIp());
            udpStream.setServerIp(frame.getDstIp());
            udpStream.setClientPort(frame.getUdpSrcPort());
            udpStream.setServerPort(frame.getUdpDstPort());
        }
        frame.setClientIp(udpStream.getClientIp());
        frame.setServerIp(udpStream.getServerIp());
        frame.setClientPort(udpStream.getClientPort());
        frame.setServerPort(udpStream.getServerPort());
        setUdpIp4IpFragment(frame, udpStream);
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
            if (udpStream.getDnsQryTime() == null) {
                udpStream.setDnsQryTime(frame.getTimestamp());
            }
            if (udpStream.getClientIp() == null) {
                udpStream.setClientIp(frame.getSrcIp());
                udpStream.setServerIp(frame.getDstIp());
            }
        } else { // 获取到第一个响应的时候，结束分析。因为之前的请求有重发的，后续的响应是对重发的请求的响应。
            // 可能没有抓到请求包
            if (udpStream.getDnsQryTime() != null) {
                frame.setDnsReplyDelay(frame.getTimestamp() - udpStream.getDnsQryTime());
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

    /**
     * UDP的长度大于1480后，会分包，此时wireshark只对最终包有udp.stream，其它分包没有udp.stream，
     * 因此找到每个分包，标记上客户端、服务端流量ip
     *
     * @param frame
     * @param udpStream
     */
    private void setUdpIp4IpFragment(FrameBean frame, UdpStream udpStream) {
        List<Integer> ipFragments = frame.getIpFragment();
        if (ipFragments != null) {
            for (Integer frameNumber : ipFragments) {
                FrameBean fragment = MapCache.getFrame(frameNumber);
                fragment.setClientIp(udpStream.getClientIp());
                fragment.setServerIp(udpStream.getServerIp());
            }
        }
    }
}
