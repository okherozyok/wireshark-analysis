package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.FrameBean;

import java.util.ArrayList;
import java.util.List;

import static com.riil.ws.analysis.common.Contants.MINUTE_BY_MS;

public class LongConnectionMetricAnalyzer {
    private List<LongConnectionMetricInterface> longConnMetrics = new ArrayList<>();

    LongConnectionMetricAnalyzer() {
    }

    void registerMetric(LongConnectionMetricInterface longConnMetric) {
        longConnMetrics.add(longConnMetric);
    }

    void start(TcpStream tcpStream) {
        FrameBean firstFrame = tcpStream.getFrames().get(0);
        tcpStream.setMetricStatisticsStartTime(firstFrame.getTimestamp());
    }

    void every(TcpStream tcpStream, FrameBean frame) {
        while (frame.getTimestamp() >= (tcpStream.getMetricStatisticsStartTime() + MINUTE_BY_MS)) {
            longConnMetrics.forEach(action -> {
                if (action.abort(tcpStream)) {
                    return;
                }
                action.segmentSettlement(tcpStream);
            });
            tcpStream.setMetricStatisticsStartTime(tcpStream.getMetricStatisticsStartTime() + MINUTE_BY_MS);
        }

        // 累加
        longConnMetrics.forEach(action -> {
            if (action.abort(tcpStream)) {
                return;
            }
            action.segmentAccumulation(tcpStream, frame);
        });
    }

    void end(TcpStream tcpStream) {
        longConnMetrics.forEach(action -> {
            if (action.abort(tcpStream)) {
                return;
            }
            action.finalSettlement(tcpStream);
        });
    }
}
