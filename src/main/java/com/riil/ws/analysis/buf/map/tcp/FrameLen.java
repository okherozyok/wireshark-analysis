package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.FrameBean;

public class FrameLen extends AbstractLongConnectionMetric implements LongConnectionMetricInterface {
    FrameLen(String index) {
        super(index);
    }

    @Override
    public boolean abort(TcpStream tcpStream) {
        return false;
    }

    @Override
    public void segmentAccumulation(TcpStream tcpStream, FrameBean frame) {
        if (frame.getFrameLength() <= 64) {
            tcpStream.addLessEq64();
        } else if (frame.getFrameLength() >= 65 && frame.getFrameLength() <= 511) {
            tcpStream.addBetween65_511();
        } else if (frame.getFrameLength() >= 512 && frame.getFrameLength() <= 1023) {
            tcpStream.addBetween512_1023();
        } else {
            tcpStream.addGreaterEq1024();
        }
    }

    @Override
    public void segmentSettlement(TcpStream tcpStream) {
        finalSettlement(tcpStream);
    }

    @Override
    public void finalSettlement(TcpStream tcpStream) {
        integerSettlement(tcpStream, tcpStream::getLessEq64, LongConnectionMetricBean::setTcpFrameLenLessEq64,
                aVoid -> tcpStream.clearLessEq64());
        integerSettlement(tcpStream, tcpStream::getBetween65_511, LongConnectionMetricBean::setTcpFrameLenBetween65_511,
                aVoid -> tcpStream.clearBetween65_511());
        integerSettlement(tcpStream, tcpStream::getBetween512_1023, LongConnectionMetricBean::setTcpFrameLenBetween512_1023,
                aVoid -> tcpStream.clearBetween512_1023());
        integerSettlement(tcpStream, tcpStream::getGreaterEq1024, LongConnectionMetricBean::setTcpFrameLenGreaterEq1024,
                aVoid -> tcpStream.clearGreaterEq1024());
    }
}
