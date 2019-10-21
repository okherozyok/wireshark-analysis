package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.FrameBean;
import com.riil.ws.analysis.buf.map.StatisticsMetricBean;
import com.riil.ws.analysis.buf.map.MapCache;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.riil.ws.analysis.buf.map.AnalyzerConstant.STATISTICS_METRIC_PREFIX;
import static com.riil.ws.analysis.buf.map.AnalyzerConstant.PACKET_INDEX_PREFIX;
import static com.riil.ws.analysis.common.Contants.UNDER_LINE;

public class ZeroWin implements LongConnectionMetricInterface {
    private String index;

    ZeroWin(String index) {
        this.index = index;
    }

    @Override
    public boolean abort(TcpStream tcpStream) {
        return false;
    }

    @Override
    public void segmentAccumulation(TcpStream tcpStream, FrameBean frame) {
        if (frame.isTcpZeroWindow()) {
            if (isClient(tcpStream, frame)) {
                segmentAcc(aVoid -> tcpStream.addClientZeroWinNum());
            } else {
                segmentAcc(aVoid -> tcpStream.addServerZeroWinNum());
            }
        }
    }

    @Override
    public void segmentSettlement(TcpStream tcpStream) {
        finalSettlement(tcpStream);
    }

    @Override
    public void finalSettlement(TcpStream tcpStream) {
        zeroWinSettlement(tcpStream, tcpStream::getMetricStatisticsStartTime, tcpStream::getClientZeroWinNum,
                StatisticsMetricBean::setTcpClientZeroWindow,
                aVoid -> tcpStream.clearClientZeroWinNum());
        zeroWinSettlement(tcpStream, tcpStream::getMetricStatisticsStartTime, tcpStream::getServerZeroWinNum,
                StatisticsMetricBean::setTcpServerZeroWindow,
                aVoid -> tcpStream.clearServerZeroWinNum());
    }

    private void segmentAcc(Consumer<Void> addZeroWinNum) {
        addZeroWinNum.accept(null);
    }

    private void zeroWinSettlement(TcpStream tcpStream, Supplier<Long> metricStatisticsStartTime, Supplier<Integer> getZeroWinNum,
                                   BiConsumer<StatisticsMetricBean, Integer> setTcpZeroWindow, Consumer<Void> clearZeroWinNum) {
        if (getZeroWinNum.get() > 0) {
            StatisticsMetricBean statMetric = getStatisticsMetricBean(tcpStream, metricStatisticsStartTime);

            if (StringUtils.isEmpty(index)) {
                statMetric.setIndex(getFirstFrame(tcpStream).getIndex().replace(PACKET_INDEX_PREFIX, STATISTICS_METRIC_PREFIX));
            } else {
                statMetric.setIndex(STATISTICS_METRIC_PREFIX + UNDER_LINE + index.trim());
            }
            statMetric.setTcpStream(tcpStream.getTcpStreamNumber());
            statMetric.setTimestamp(metricStatisticsStartTime.get());
            setTcpZeroWindow.accept(statMetric, getZeroWinNum.get());
            statMetric.setClientIp(tcpStream.getClientIpByFirst());
            statMetric.setServerIp(tcpStream.getServerIpByFirst());
            statMetric.setClientPort(tcpStream.getClientPortByFirst());
            statMetric.setServerPort(tcpStream.getServerPortByFirst());
            clearZeroWinNum.accept(null);
        }
    }

    private boolean isClient(TcpStream tcpStream, FrameBean frame) {
        return tcpStream.getClientIpByFirst().equals(frame.getSrcIp());
    }

    private FrameBean getFirstFrame(TcpStream tcpStream) {
        return tcpStream.getFrames().get(0);
    }
}
