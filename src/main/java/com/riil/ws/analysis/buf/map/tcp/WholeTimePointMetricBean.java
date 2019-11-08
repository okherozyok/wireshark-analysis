package com.riil.ws.analysis.buf.map.tcp;

import com.alibaba.fastjson.annotation.JSONField;
import com.riil.ws.analysis.buf.map.FrameConstant;
import com.riil.ws.analysis.common.IpV4Util;

public class WholeTimePointMetricBean extends AbstractMetricBean {
    private Integer onlineUserInt;

    @JSONField(name = FrameConstant.ONLINE_USER)
    public String getOnlineUser() {
        if (onlineUserInt != null) {
            return IpV4Util.ipInt2Str(onlineUserInt);
        }

        return null;
    }

    public void setOnlineUser(String onlineUser) {
        if (onlineUser == null) {
            onlineUserInt = null;
        } else {
            onlineUserInt = IpV4Util.ipStr2Int(onlineUser);
        }
    }
}
