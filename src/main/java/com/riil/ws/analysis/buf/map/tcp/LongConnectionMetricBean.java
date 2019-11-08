package com.riil.ws.analysis.buf.map.tcp;

import com.alibaba.fastjson.annotation.JSONField;
import com.riil.ws.analysis.buf.map.FrameConstant;
import com.riil.ws.analysis.common.IpV4Util;

public class LongConnectionMetricBean extends AbstractMetricBean {

    @JSONField(name = FrameConstant.TCP_CLIENT_ZERO_WINDOW)
    private int tcpClientZeroWindow;
    @JSONField(name = FrameConstant.TCP_SERVER_ZERO_WINDOW)
    private int tcpServerZeroWindow;

    @JSONField(name = FrameConstant.TCP_FRAME_LEN_LESS_EQ_64)
    private int tcpFrameLenLessEq64;
    @JSONField(name = FrameConstant.TCP_FRAME_LEN_BETWEEN_65_511)
    private int tcpFrameLenBetween65_511;
    @JSONField(name = FrameConstant.TCP_FRAME_LEN_BETWEEN_512_1023)
    private int tcpFrameLenBetween512_1023;
    @JSONField(name = FrameConstant.TCP_FRAME_LEN_GREATER_EQ_1024)
    private int tcpFrameLenGreaterEq1024;

    public int getTcpClientZeroWindow() {
        return tcpClientZeroWindow;
    }

    public void setTcpClientZeroWindow(int tcpClientZeroWindow) {
        this.tcpClientZeroWindow = tcpClientZeroWindow;
    }

    public int getTcpServerZeroWindow() {
        return tcpServerZeroWindow;
    }

    public void setTcpServerZeroWindow(int tcpServerZeroWindow) {
        this.tcpServerZeroWindow = tcpServerZeroWindow;
    }

    public int getTcpFrameLenLessEq64() {
        return tcpFrameLenLessEq64;
    }

    public void setTcpFrameLenLessEq64(int tcpFrameLenLessEq64) {
        this.tcpFrameLenLessEq64 = tcpFrameLenLessEq64;
    }

    public int getTcpFrameLenBetween65_511() {
        return tcpFrameLenBetween65_511;
    }

    public void setTcpFrameLenBetween65_511(int tcpFrameLenBetween65_511) {
        this.tcpFrameLenBetween65_511 = tcpFrameLenBetween65_511;
    }

    public int getTcpFrameLenBetween512_1023() {
        return tcpFrameLenBetween512_1023;
    }

    public void setTcpFrameLenBetween512_1023(int tcpFrameLenBetween512_1023) {
        this.tcpFrameLenBetween512_1023 = tcpFrameLenBetween512_1023;
    }

    public int getTcpFrameLenGreaterEq1024() {
        return tcpFrameLenGreaterEq1024;
    }

    public void setTcpFrameLenGreaterEq1024(int tcpFrameLenGreaterEq1024) {
        this.tcpFrameLenGreaterEq1024 = tcpFrameLenGreaterEq1024;
    }
}
