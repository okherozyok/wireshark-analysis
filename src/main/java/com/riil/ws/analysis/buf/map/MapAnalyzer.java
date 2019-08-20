package com.riil.ws.analysis.buf.map;

import java.io.FileWriter;
import java.util.Map;
import java.util.Set;

import com.riil.ws.analysis.buf.map.dns.DnsSession;
import com.riil.ws.analysis.buf.map.tcp.TcpAnalyzer;
import com.riil.ws.analysis.buf.map.tcp.TcpStream;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.riil.ws.analysis.buf.IAnalyzer;

import static com.riil.ws.analysis.buf.map.AnalyzerConstant.*;
import static com.riil.ws.analysis.buf.map.AnalyzerConstant.generateIndexJson;

@Service
public class MapAnalyzer implements IAnalyzer {
    private final Logger LOGGER = LoggerFactory.getLogger(MapAnalyzer.class);

    @Value("${output.to}")
    private String outputTo;

    @Value("${output.file.name}")
    private String outputToFileName;

    @Value("${output.file.lineSeparator}")
    private String lineSeparator;

    @Value("${output.es.bulkSize}")
    private int outputToESBulkSize;

    @Value("${output.es.host}")
    private String ESHost;

    @Value("${output.es.port}")
    private int ESPort;

    @Autowired
    private Adapter adapter;

    @Autowired
    private TcpAnalyzer tcpAnalyzer;

    private RestHighLevelClient client;

    public void save(String indexLineJson, String frameLineJson) throws Exception {
        FrameBean frame = adapter.esJson2Bean(indexLineJson, frameLineJson);
        MapCache.putFrame(frame.getFrameNumber(), frame);

        Integer tcpStreamNumber = frame.getTcpStreamNumber();
        if (tcpStreamNumber != null && frame.isTcp()) {
            TcpStream tcpStream = MapCache.getTcpStream(tcpStreamNumber);
            if (tcpStream == null) {
                tcpStream = new TcpStream(tcpStreamNumber);
                MapCache.putTcpStream(tcpStream);
            }
            tcpStream.append(frame);
        }

        Boolean dnsQryHost = frame.getDnsQryHost();
        if(Boolean.TRUE.equals(dnsQryHost)) {
            int dnsId = frame.getDnsId();
            DnsSession dnsSession = MapCache.getDnsSession(dnsId);
            if(dnsSession == null) {
                dnsSession = new DnsSession(dnsId);
                MapCache.putDnsSession(dnsSession);
            }
            dnsSession.append(frame);
        }
    }

    @Override
    public void analysis() throws Exception {
        Set<Integer> keySet = MapCache.getTcpStreamMap().keySet();
        for (Integer tcpStreamNumber : keySet) {
            TcpStream tcpStream = MapCache.getTcpStream(tcpStreamNumber);
            tcpAnalyzer.analysis(tcpStream);
        }
    }

    @Override
    public void output() throws Exception {
        if (outputTo.equals(OUTPUT_TO_ES)) {
            output2ES();
            //output2ESConcurrentConn();
        } else if (outputTo.equals(OUTPUT_TO_FILE)) {
            output2File();
            output2FileConcurrentConn();
        } else {
            throw new Exception("Unknown output to : " + outputTo);
        }

    }

    private void output2File() throws Exception {
        String linSep = getLineSeparator();

        try (FileWriter fw = new FileWriter(outputToFileName)) {
            int size = MapCache.getFrameMap().size();
            for (int i = 1; i <= size; i++) {
                fw.append(adapter.esBean2IndexJson(MapCache.getFrameMap().get(i))).append(linSep)
                        .append(adapter.esBean2FrameJson(MapCache.getFrameMap().get(i))).append(linSep);
            }
        }
    }

    private void output2FileConcurrentConn() throws Exception {
        String linSep = getLineSeparator();

        try (FileWriter fw = new FileWriter(TCP_CONCURRENT_CONN_INDEX_PREFIX + outputToFileName)) {
            Map<Long, Map<Long, ConcurrentConnBean>> concurrentConnCache = MapCache.getConcurrentConnCache();
            for (Long ipPortKey : concurrentConnCache.keySet()) {
                Map<Long, ConcurrentConnBean> concurrentConnBeanMap = concurrentConnCache.get(ipPortKey);
                for (Long timestamp : concurrentConnBeanMap.keySet()) {
                    fw.append(generateIndexJson(concurrentConnBeanMap.get(timestamp).getIndex())).append(linSep)
                            .append(JSON.toJSONString(concurrentConnBeanMap.get(timestamp))).append(linSep);
                }
            }
        }
    }

    private void output2ES() throws Exception {

        RestHighLevelClient client = newRestHighLevelClient();

        BulkRequest bulkRequest = new BulkRequest();

        int size = MapCache.getFrameMap().size();
        for (int i = 1; i <= size; i++) {
            FrameBean frame = MapCache.getFrameMap().get(i);

            IndexRequest indexRequest = new IndexRequest(frame.getIndex());
            indexRequest.source(adapter.esBean2FrameJson(frame), XContentType.JSON);

            bulkRequest.add(indexRequest);

            if (i % outputToESBulkSize == 0) {
                bulk2ES(client, bulkRequest);
                bulkRequest = new BulkRequest();
            }
        }
        if (bulkRequest.numberOfActions() > 0) {
            bulk2ES(client, bulkRequest);
        }

    }

    private void output2ESConcurrentConn() throws Exception {
        RestHighLevelClient client = newRestHighLevelClient();
        BulkRequest bulkRequest = new BulkRequest();
        int count = 0;

        Map<Long, Map<Long, ConcurrentConnBean>> concurrentConnCache = MapCache.getConcurrentConnCache();
        for (Long ipPortKey : concurrentConnCache.keySet()) {
            Map<Long, ConcurrentConnBean> concurrentConnBeanMap = concurrentConnCache.get(ipPortKey);
            for (Long timestamp : concurrentConnBeanMap.keySet()) {
                ConcurrentConnBean concurrentConnBean = concurrentConnBeanMap.get(timestamp);

                IndexRequest indexRequest = new IndexRequest(concurrentConnBean.getIndex());
                indexRequest.source(JSON.toJSONString(concurrentConnBean), XContentType.JSON);
                bulkRequest.add(indexRequest);
                count++;

                if (count % outputToESBulkSize == 0) {
                    bulk2ES(client, bulkRequest);
                    bulkRequest = new BulkRequest();
                }
            }

        }

        if (bulkRequest.numberOfActions() > 0) {
            bulk2ES(client, bulkRequest);
        }

    }

    private RestHighLevelClient newRestHighLevelClient() {
        if (client == null) {
            return new RestHighLevelClient(RestClient.builder(new HttpHost(ESHost, ESPort, "http")));

        }
        return client;
    }

    private void bulk2ES(RestHighLevelClient client, BulkRequest bulkRequest) throws Exception {
        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        if (response.hasFailures()) {
            throw new Exception(response.buildFailureMessage());
        }
    }

    private String getLineSeparator() throws Exception {
        if (lineSeparator.equals(WINDOWS_LINE_SEPAROTOR)) {
            return "\r\n";
        } else if (lineSeparator.equals(LINUX_LINE_SEPAROTOR)) {
            return "\n";
        } else {
            throw new Exception("Unknown line separator: " + lineSeparator);
        }
    }
}
