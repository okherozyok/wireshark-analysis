package com.riil.ws.analysis.buf.map;

public final class FrameConstant {
	// 值
	public static final String SIGN_PLACEHOLDER = "1";
	public static final Integer ICMP_PROTO_NUM = 1;
	public static final Integer TCP_PROTO_NUM = 6;
	
	// 结构
	public static final String LAYERS = "layers";
	
	// 属性
	public static final String FRAME_NUMBER = "frame_number";
	public static final String TCP_STREAM = "tcp_stream";
	public static final String TIMESTAMP = "timestamp";
	public static final String IP_SRC = "ip_src";
	public static final String IP_DST = "ip_dst";
	public static final String TCP_SEQ = "tcp_seq";
	public static final String TCP_ACK = "tcp_ack";
	
	// 指标，wireshark已有的
	public static final String IP_PROTO = "ip_proto";
	public static final String TCP_LEN = "tcp_len";
	public static final String TCP_ANALYSIS_ACKS_FRAME = "tcp_analysis_acks_frame";
	public static final String TCP_ANALYSIS_ACK_RTT = "tcp_analysis_ack_rtt";
	public static final String TCP_CONNECTION_SYN = "tcp_connection_syn";
	public static final String TCP_CONNECTION_SACK = "tcp_connection_sack";
	public static final String TCP_CONNECTION_RST = "tcp_connection_rst";
	public static final String TCP_SEGMENT = "tcp_segment";
	public static final String HTTP_REQUEST = "http_request";

	// 指标，补充的
	public static final String CLIENT_IP = "client_ip";
	public static final String SERVER_IP = "server_ip";
	public static final String TCP = "tcp";
	public static final String TCP_CLIENT_CONNECTION_RST = "tcp_client_connection_rst";
	public static final String TCP_SERVER_CONNECTION_RST = "tcp_server_connection_rst";
	public static final String TCP_CLIENT_CONNECTION_NO_RESP = "tcp_client_connection_no_resp";
	public static final String TCP_SERVER_CONNECTION_NO_RESP = "tcp_server_connection_no_resp";
	public static final String TCP_CONNECTION_SUCCESS = "tcp_connection_success";
	public static final String TCP_CLIENT_CONNECTION_DELAY = "tcp_client_connection_delay";
	public static final String TCP_SERVER_CONNECTION_DELAY = "tcp_server_connection_delay";
	public static final String TCP_CONNECTION_DELAY = "tcp_connection_delay";
	public static final String TCP_UP_RTT = "tcp_up_rtt";
	public static final String TCP_DOWN_RTT = "tcp_down_rtt";
	public static final String HTTP_REQ_TRANS_DELAY = "http_req_trans_delay";

}
