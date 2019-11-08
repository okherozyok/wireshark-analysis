package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.MapCache;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.riil.ws.analysis.buf.map.AnalyzerConstant.PACKET_INDEX_PREFIX;
import static com.riil.ws.analysis.buf.map.AnalyzerConstant.WHOLE_TIME_POINT_METRIC_PREFIX;
import static com.riil.ws.analysis.common.Contants.UNDER_LINE;

public class AbstractWholeTimePointMetric extends AbstractMetric {
    protected long startWholeTimePoint;

    protected AbstractWholeTimePointMetric(String index) {
        super(index);
    }

    protected WholeTimePointMetricBean getWholeTimePointMetric(TcpStream tcpStream, long startWholeTimePoint) {
        Map<Integer, Map<Long, WholeTimePointMetricBean>> wholePointMetricCache = MapCache.getWholeTimePointMetricCache();
        Map<Long, WholeTimePointMetricBean> timeStampMap =
                wholePointMetricCache.computeIfAbsent(tcpStream.getTcpStreamNumber(), aVoid -> new LinkedHashMap<>());
        WholeTimePointMetricBean metricBean = timeStampMap.get(startWholeTimePoint);
        if (metricBean == null) {
            metricBean = new WholeTimePointMetricBean();
            if (StringUtils.isEmpty(index)) {
                metricBean.setIndex(tcpStream.getFrames().get(0).getIndex().replace(PACKET_INDEX_PREFIX, WHOLE_TIME_POINT_METRIC_PREFIX));
            } else {
                metricBean.setIndex(WHOLE_TIME_POINT_METRIC_PREFIX + UNDER_LINE + index.trim());
            }
            metricBean.setTcpStream(tcpStream.getTcpStreamNumber());
            metricBean.setTimestamp(startWholeTimePoint);
            metricBean.setClientIp(tcpStream.getClientIp());
            metricBean.setServerIp(tcpStream.getServerIp());
            metricBean.setClientPort(tcpStream.getClientPort());
            metricBean.setServerPort(tcpStream.getServerPort());
            timeStampMap.put(startWholeTimePoint, metricBean);
        }

        return metricBean;
    }
}
