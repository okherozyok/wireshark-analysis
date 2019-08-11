package com.riil.ws.analysis.buf.map;

import com.alibaba.fastjson.JSON;
import com.riil.ws.analysis.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class AdapterTest extends BaseTest {
    @Autowired
    private Adapter adapter;

    @Test
    public void esJson2Bean() {
        String index="{\"index\":{\"_index\":\"packets-2019-08-09\",\"_type\":\"pcap_file\"}}";
        String json = "{\"timestamp\":\"1565344155065\",\"layers\":{\"frame_number\":[\"17\"],\"frame_len\":[\"70\"],\"ip_proto\":[\"6\"],\"ip_src\":[\"172.16.119.70\"],\"ip_dst\":[\"17.42.254.13\"],\"tcp_stream\":[\"0\"],\"tcp_srcport\":[\"56754\"],\"tcp_dstport\":[\"443\"],\"tcp_seq\":[\"3698\"],\"tcp_ack\":[\"131\"],\"tcp_len\":[\"0\"],\"tcp_analysis_acks_frame\":[\"15\"],\"tcp_analysis_ack_rtt\":[\"0.003642000\"],\"tcp_analysis_duplicate_ack\":[\"1\",\"1\"]}}";
        FrameBean frameBean = adapter.esJson2Bean(index, json);
        System.out.println(JSON.toJSONString(frameBean));
    }
}