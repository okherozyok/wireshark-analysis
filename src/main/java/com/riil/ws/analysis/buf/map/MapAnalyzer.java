package com.riil.ws.analysis.buf.map;

import java.io.FileWriter;
import java.util.Set;

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
import com.alibaba.fastjson.JSONObject;
import com.riil.ws.analysis.buf.IAnalyzer;

@Service
public class MapAnalyzer implements IAnalyzer {
    private final Logger LOGGER = LoggerFactory.getLogger(MapAnalyzer.class);
    private final String WINDOWS_LINE_SEPAROTOR = "rn";
    private final String LINUX_LINE_SEPAROTOR = "n";
    private final String OUTPUT_TO_ES = "ES";
    private final String OUTPUT_TO_FILE = "File";

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
        } else if (outputTo.equals(OUTPUT_TO_FILE)) {
            output2File();
        } else {
            throw new Exception("Unknown output to : " + outputTo);
        }

    }

    private void output2File() throws Exception {
        if (lineSeparator.equals(WINDOWS_LINE_SEPAROTOR)) {
            lineSeparator = "\r\n";
        } else if (lineSeparator.equals(LINUX_LINE_SEPAROTOR)) {
            lineSeparator = "\n";
        } else {
            throw new Exception("Unknown line separator: " + lineSeparator);
        }

        try (FileWriter fw = new FileWriter("output2es.json")) {
            int size = MapCache.getFrameMap().size();
            for (int i = 1; i <= size; i++) {
                fw.append(adapter.esBean2IndexJson(MapCache.getFrameMap().get(i))).append(lineSeparator)
                        .append(adapter.esBean2FrameJson(MapCache.getFrameMap().get(i))).append(lineSeparator);
            }
        }
    }

    private void output2ES() throws Exception {

        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(ESHost, ESPort, "http")));

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

    private void bulk2ES(RestHighLevelClient client, BulkRequest bulkRequest) throws Exception {
        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        if (response.hasFailures()) {
            throw new Exception(response.buildFailureMessage());
        }
    }
}
