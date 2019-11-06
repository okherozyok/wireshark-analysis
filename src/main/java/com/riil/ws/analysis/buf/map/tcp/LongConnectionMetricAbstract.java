package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.MapCache;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.riil.ws.analysis.buf.map.AnalyzerConstant.PACKET_INDEX_PREFIX;
import static com.riil.ws.analysis.buf.map.AnalyzerConstant.STATISTICS_METRIC_PREFIX;
import static com.riil.ws.analysis.common.Contants.UNDER_LINE;

public abstract class LongConnectionMetricAbstract {
    protected String index;

    protected void integerSettlement(TcpStream tcpStream, Supplier<Integer> getNum,
                           BiConsumer<StatisticsMetricBean, Integer> setTcpNum, Consumer<Void> clearNum) {
        if (getNum.get() >= 0) {
            StatisticsMetricBean statMetric = getStatisticsMetricBean(tcpStream);

            setTcpNum.accept(statMetric, getNum.get());
            clearNum.accept(null);
        }
    }

    protected StatisticsMetricBean getStatisticsMetricBean(TcpStream tcpStream) {
        Map<Integer, Map<Long, StatisticsMetricBean>> statisticsMetricCache = MapCache.getStatisticsMetricCache();
        Map<Long, StatisticsMetricBean> timeStampStatMetricMap =
                statisticsMetricCache.computeIfAbsent(tcpStream.getTcpStreamNumber(), aVoid -> new LinkedHashMap<>());
        StatisticsMetricBean statMetric = timeStampStatMetricMap.get(tcpStream.getMetricStatisticsStartTime());
        if (statMetric == null) {
            statMetric = new StatisticsMetricBean();
            if (StringUtils.isEmpty(index)) {
                statMetric.setIndex(tcpStream.getFrames().get(0).getIndex().replace(PACKET_INDEX_PREFIX, STATISTICS_METRIC_PREFIX));
            } else {
                statMetric.setIndex(STATISTICS_METRIC_PREFIX + UNDER_LINE + index.trim());
            }
            statMetric.setTcpStream(tcpStream.getTcpStreamNumber());
            statMetric.setTimestamp(tcpStream.getMetricStatisticsStartTime());
            statMetric.setClientIp(tcpStream.getClientIp());
            statMetric.setServerIp(tcpStream.getServerIp());
            statMetric.setClientPort(tcpStream.getClientPort());
            statMetric.setServerPort(tcpStream.getServerPort());
            timeStampStatMetricMap.put(tcpStream.getMetricStatisticsStartTime(), statMetric);
        }

        return statMetric;
    }
}
