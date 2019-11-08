package com.riil.ws.analysis.buf.map.tcp;

import com.riil.ws.analysis.buf.map.FrameBean;

import java.util.ArrayList;
import java.util.List;

public class WholeTimePointMetricAnalyzer {
    private List<WholeTimePointMetricInterface> wholeTimePointMetrics = new ArrayList<>();

    void registerMetric(WholeTimePointMetricInterface wholeTimePointMetric) {
        wholeTimePointMetrics.add(wholeTimePointMetric);
    }

    void start(TcpStream tcpStream) {
        FrameBean firstFrame = tcpStream.getFrames().get(0);
        wholeTimePointMetrics.forEach(action -> {
            long wholeTimePoint = action.getWholeTimePoint();
            long startWholeTimePoint = firstFrame.getTimestamp() / wholeTimePoint * wholeTimePoint;
            action.setStartWholeTimePoint(startWholeTimePoint);
        });
    }

    void every(TcpStream tcpStream, FrameBean frame) {
        wholeTimePointMetrics.forEach(action -> {
            long startWholeTimePoint = action.getStartWholeTimePoint();
            long wholeTimePoint = action.getWholeTimePoint();
            long frameTimePoint = frame.getTimestamp() / wholeTimePoint * wholeTimePoint;
            while (frameTimePoint > startWholeTimePoint) {
                action.segmentSettlement(tcpStream);
                startWholeTimePoint += wholeTimePoint;
                action.setStartWholeTimePoint(startWholeTimePoint);
            }

            action.segmentAccumulation(tcpStream, frame);
        });
    }

    void end(TcpStream tcpStream) {
        wholeTimePointMetrics.forEach(action -> {
            action.finalSettlement(tcpStream);
        });
    }
}
