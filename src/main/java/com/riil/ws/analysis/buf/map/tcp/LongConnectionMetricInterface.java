package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.FrameBean;

public interface LongConnectionMetricInterface {
    boolean abort(TcpStream tcpStream);

    void segmentAccumulation(TcpStream tcpStream, FrameBean frame);

    void segmentSettlement(TcpStream tcpStream);

    void finalSettlement(TcpStream tcpStream);
}
