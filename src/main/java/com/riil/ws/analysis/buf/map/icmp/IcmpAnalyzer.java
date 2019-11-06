package com.riil.ws.analysis.buf.map.icmp;

import com.riil.ws.analysis.buf.map.FrameBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IcmpAnalyzer {
    private final Logger LOGGER = LoggerFactory.getLogger(IcmpAnalyzer.class);

    public void analysis(FrameBean frame) throws Exception {
        markIpDirectionByFirst(frame);
    }

    /**
     * ICMP中的客户端、服务端方向判断，需要根据经验补充
     *
     * @param frame
     */
    private void markIpDirectionByFirst(FrameBean frame) {
        short icmpType = frame.getIcmpType();
        short icmpCode = frame.getIcmpCode();

        // ping request
        if (icmpType == 8 && icmpCode == 0) {
            frame.setClientIp(frame.getSrcIp());
            frame.setServerIp(frame.getDstIp());
        } else if (icmpType == 0 && icmpCode == 0) { // ping response
            frame.setClientIp(frame.getDstIp());
            frame.setServerIp(frame.getSrcIp());
        }
    }
}
