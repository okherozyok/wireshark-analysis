package com.riil.ws.analysis.buf.map.tcp;

public abstract class AbstractMetric {
    protected String index;

    protected AbstractMetric(String index) {
        this.index = index;
    }
}
