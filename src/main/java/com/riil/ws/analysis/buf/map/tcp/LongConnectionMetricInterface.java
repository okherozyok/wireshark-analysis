package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.FrameBean;
import com.riil.ws.analysis.buf.map.MapCache;
import com.riil.ws.analysis.buf.map.StatisticsMetricBean;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public interface LongConnectionMetricInterface {
    boolean abort(TcpStream tcpStream);

    void segmentAccumulation(TcpStream tcpStream, FrameBean frame);

    void segmentSettlement(TcpStream tcpStream);

    void finalSettlement(TcpStream tcpStream);

    default StatisticsMetricBean getStatisticsMetricBean(TcpStream tcpStream, Supplier<Long> metricStatisticsStartTime) {
        Map<Integer, Map<Long, StatisticsMetricBean>> statisticsMetricCache = MapCache.getStatisticsMetricCache();
        Map<Long, StatisticsMetricBean> timeStampIncMetricMap = statisticsMetricCache.get(tcpStream.getTcpStreamNumber());
        StatisticsMetricBean statMetric = null;
        if (timeStampIncMetricMap == null) {
            statMetric = new StatisticsMetricBean();
            timeStampIncMetricMap = new LinkedHashMap<>();
            timeStampIncMetricMap.put(metricStatisticsStartTime.get(), statMetric);
            statisticsMetricCache.put(tcpStream.getTcpStreamNumber(), timeStampIncMetricMap);
        }
        statMetric = timeStampIncMetricMap.get(metricStatisticsStartTime.get());
        if (statMetric == null) {
            statMetric = new StatisticsMetricBean();
            timeStampIncMetricMap.put(metricStatisticsStartTime.get(), statMetric);
        }

        return statMetric;
    }
}
