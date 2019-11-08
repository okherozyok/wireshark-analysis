package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.FrameBean;
import org.springframework.util.StringUtils;

import static com.riil.ws.analysis.common.Contants.MINUTE_BY_MS;

public class OnlineUser extends AbstractWholeTimePointMetric implements WholeTimePointMetricInterface {
    protected OnlineUser(String index) {
        super(index);
    }

    @Override
    public void setStartWholeTimePoint(long timestamp) {
        startWholeTimePoint = timestamp;
    }

    @Override
    public long getStartWholeTimePoint() {
        return startWholeTimePoint;
    }

    @Override
    public long getWholeTimePoint() {
        return MINUTE_BY_MS;
    }

    @Override
    public void segmentAccumulation(TcpStream tcpStream, FrameBean frame) {
        if(StringUtils.isEmpty(tcpStream.getOnlineUserIp())) {
            tcpStream.setOnlineUserIp(tcpStream.getClientIp());
        }
    }

    @Override
    public void segmentSettlement(TcpStream tcpStream) {
        finalSettlement(tcpStream);
    }

    @Override
    public void finalSettlement(TcpStream tcpStream) {
        WholeTimePointMetricBean bean = getWholeTimePointMetric(tcpStream, startWholeTimePoint);
        bean.setOnlineUser(tcpStream.getOnlineUserIp());
    }
}
