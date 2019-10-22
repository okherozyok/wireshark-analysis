package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.FrameBean;

public class FrameLen extends LongConnectionMetricAbstract implements LongConnectionMetricInterface {
    FrameLen(String index) {
        this.index = index;
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
        integerSettlement(tcpStream, tcpStream::getLessEq64, StatisticsMetricBean::setTcpFrameLenLessEq64,
                aVoid -> tcpStream.clearLessEq64());
        integerSettlement(tcpStream, tcpStream::getBetween65_511, StatisticsMetricBean::setTcpFrameLenBetween65_511,
                aVoid -> tcpStream.clearBetween65_511());
        integerSettlement(tcpStream, tcpStream::getBetween512_1023, StatisticsMetricBean::setTcpFrameLenBetween512_1023,
                aVoid -> tcpStream.clearBetween512_1023());
        integerSettlement(tcpStream, tcpStream::getGreaterEq1024, StatisticsMetricBean::setTcpFrameLenGreaterEq1024,
                aVoid -> tcpStream.clearGreaterEq1024());
    }
}
