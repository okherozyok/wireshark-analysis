package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.MapCache;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.riil.ws.analysis.buf.map.AnalyzerConstant.PACKET_INDEX_PREFIX;
import static com.riil.ws.analysis.buf.map.AnalyzerConstant.LONG_CONN_METRIC_PREFIX;
import static com.riil.ws.analysis.common.Contants.UNDER_LINE;

public abstract class AbstractLongConnectionMetric extends AbstractMetric {
    protected AbstractLongConnectionMetric(String index) {
        super(index);
    }

    protected void integerSettlement(TcpStream tcpStream, Supplier<Integer> getNum,
                                     BiConsumer<LongConnectionMetricBean, Integer> setTcpNum, Consumer<Void> clearNum) {
        if (getNum.get() >= 0) {
            LongConnectionMetricBean statMetric = getLongConnectionMetric(tcpStream);

            setTcpNum.accept(statMetric, getNum.get());
            clearNum.accept(null);
        }
    }

    protected LongConnectionMetricBean getLongConnectionMetric(TcpStream tcpStream) {
        Map<Integer, Map<Long, LongConnectionMetricBean>> longConnMetricCache = MapCache.getLongConnectionMetricCache();
        Map<Long, LongConnectionMetricBean> timeStampMap =
                longConnMetricCache.computeIfAbsent(tcpStream.getTcpStreamNumber(), aVoid -> new LinkedHashMap<>());
        LongConnectionMetricBean longConnMetric = timeStampMap.get(tcpStream.getLongConnMetricStartTime());
        if (longConnMetric == null) {
            longConnMetric = new LongConnectionMetricBean();
            if (StringUtils.isEmpty(index)) {
                longConnMetric.setIndex(tcpStream.getFrames().get(0).getIndex().replace(PACKET_INDEX_PREFIX, LONG_CONN_METRIC_PREFIX));
            } else {
                longConnMetric.setIndex(LONG_CONN_METRIC_PREFIX + UNDER_LINE + index.trim());
            }
            longConnMetric.setTcpStream(tcpStream.getTcpStreamNumber());
            longConnMetric.setTimestamp(tcpStream.getLongConnMetricStartTime());
            longConnMetric.setClientIp(tcpStream.getClientIp());
            longConnMetric.setServerIp(tcpStream.getServerIp());
            longConnMetric.setClientPort(tcpStream.getClientPort());
            longConnMetric.setServerPort(tcpStream.getServerPort());
            timeStampMap.put(tcpStream.getLongConnMetricStartTime(), longConnMetric);
        }

        return longConnMetric;
    }
}
