package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.FrameBean;

public interface WholeTimePointMetricInterface {
    void setStartWholeTimePoint(long timestamp);

    long getStartWholeTimePoint();

    long getWholeTimePoint();

    void segmentAccumulation(TcpStream tcpStream, FrameBean frame);

    void segmentSettlement(TcpStream tcpStream);

    void finalSettlement(TcpStream tcpStream);
}
