package com.riil.ws.analysis.buf.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Adapter {
    private final Logger LOGGER = LoggerFactory.getLogger(Adapter.class);
    private JSONObject indexJson;
    private JSONObject json;
    private FrameBean frame;

    public FrameBean esJson2Bean(String esIndexJson, String frameJson) {
        frame = new FrameBean();
        indexJson = JSON.parseObject(esIndexJson);
        setIndex();

        json = JSON.parseObject(frameJson);
        setTimeStamp();
        setFrameProto();
        setIsTcpConnectionSyn();
        setSrcIp();

        return frame;
    }

    private void setIndex() {
        frame.setIndex((String) ((JSONObject) indexJson.get("index")).get("_index"));
    }

    private void setTimeStamp() {
        frame.setTimestamp(Long.parseLong((String) json.get("timestamp")));
    }

    /**
     * 由于ip_protoc 是 协议数组，取第一个协议为该frame的协议
     */
    @SuppressWarnings("unchecked")
    public void setFrameProto() {
        Object protos = getLayerBy(FrameConstant.IP_PROTO);
        if (protos != null) {
            if (FrameConstant.TCP_PROTO_NUM.equals(Integer.valueOf(((List<String>) protos).get(0)))) {
                frame.setTcp(true);
            }
        }
    }

    private void setIsTcpConnectionSyn() {
        Object tcpConnectionSyn = getLayerBy(FrameConstant.TCP_CONNECTION_SYN);
        if (tcpConnectionSyn != null) {
            frame.setTcpConnectionSyn(true);
        }
    }

    private void setSrcIp() {
        frame.getLayers().setSrcIp(getLayerFirstBy(FrameConstant.IP_SRC));
    }

    @SuppressWarnings("unchecked")
    private Integer getIntegerLayerFirstBy(String metric) {
        Object o = getLayerBy(metric);
        if (o != null) {
            return Integer.valueOf(((List<String>) o).get(0));
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private String getLayerFirstBy(String metric) {
        return ((List<String>) getLayerBy(metric)).get(0);
    }

    private Object getLayerBy(String metric) {
        return ((JSONObject) json.get(FrameConstant.LAYERS)).get(metric);
    }

    private void delLayerBy(String metric) {
        ((JSONObject) json.get(FrameConstant.LAYERS)).remove(metric);
    }
}
