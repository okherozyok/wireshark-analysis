package com.riil.ws.analysis.buf.map;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author GlobalZ
 */
public class Frame {
    private String esIndexJson;
    private String frameJson;

    public String getEsIndexJson() {
        return esIndexJson;
    }

    public void setEsIndexJson(String esIndexJson) {
        this.esIndexJson = esIndexJson;
    }

    public String getFrameJson() {
        return frameJson;
    }

    public void setFrameJson(String frameJson) {
        this.frameJson = frameJson;
    }

    public void setFrameJson(FrameJsonObject jsonObject) {
        setFrameJson(jsonObject.toJsonString());
    }

    public FrameJsonObject toJsonObject() {
        JSONObject parseObject = JSON.parseObject(getFrameJson());
        return new FrameJsonObject(parseObject);
    }

}
