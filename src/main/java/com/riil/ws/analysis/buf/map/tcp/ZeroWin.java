package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.FrameBean;

public class ZeroWin extends AbstractLongConnectionMetric implements LongConnectionMetricInterface {
    ZeroWin(String index) {
        super(index);
    }

    @Override
    public boolean abort(TcpStream tcpStream) {
        return false;
    }

    @Override
    public void segmentAccumulation(TcpStream tcpStream, FrameBean frame) {
        if (frame.isTcpZeroWindow()) {
            if (isClient(tcpStream, frame)) {
                tcpStream.addClientZeroWinNum();
            } else {
                tcpStream.addServerZeroWinNum();
            }
        }
    }

    @Override
    public void segmentSettlement(TcpStream tcpStream) {
        finalSettlement(tcpStream);
    }

    @Override
    public void finalSettlement(TcpStream tcpStream) {
        integerSettlement(tcpStream, tcpStream::getClientZeroWinNum, LongConnectionMetricBean::setTcpClientZeroWindow,
                aVoid -> tcpStream.clearClientZeroWinNum());
        integerSettlement(tcpStream, tcpStream::getServerZeroWinNum, LongConnectionMetricBean::setTcpServerZeroWindow,
                aVoid -> tcpStream.clearServerZeroWinNum());
    }

    private boolean isClient(TcpStream tcpStream, FrameBean frame) {
        return tcpStream.getClientIp().equals(frame.getSrcIp());
    }
}
