package com.riil.ws.analysis.buf.map;

public final class AnalyzerConstant {
    public static final String WINDOWS_LINE_SEPAROTOR = "rn";
    public static final String LINUX_LINE_SEPAROTOR = "n";
    public static final String OUTPUT_TO_ES = "ES";
    public static final String OUTPUT_TO_FILE = "File";
    public static final String PACKET_INDEX_PREFIX="packets";
    public static final String LONG_CONN_METRIC_PREFIX ="long_conn_metric";
    public static final String WHOLE_TIME_POINT_METRIC_PREFIX ="whole_time_point_metric";
    public static final String TCP_CONCURRENT_CONN_INDEX_PREFIX="tcp_concurrent_conn";
    public static final String HTTP_CONCURRENT_REQ_INDEX_PREFIX ="http_concurrent_req";

    public static final String INDEX="index";
    public static final String _INDEX="_index";
    public static String generateIndexJson(String index) {
        return "{\"index\":{\"_index\":\"" + index + "\"}}";
    }
}
