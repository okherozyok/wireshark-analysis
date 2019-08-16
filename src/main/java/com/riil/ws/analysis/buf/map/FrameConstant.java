package com.riil.ws.analysis.buf.map;

public final class FrameConstant {
	// 值
	public static final Integer TCP_PROTO_NUM = 6;
	public static final String ZERO_STRING="0";
	
	// 结构
	public static final String LAYERS = "layers";

	// 指标，wireshark已有的
	public static final String TIMESTAMP = "timestamp";
	public static final String FRAME_NUMBER = "frame_number";
	public static final String FRAME_LEN = "frame_len";
	public static final String IP_PROTO = "ip_proto";
	public static final String TCP_STREAM = "tcp_stream";
	public static final String TCP_SRCPORT = "tcp_srcport";
	public static final String TCP_DSTPORT = "tcp_dstport";
	public static final String IP_SRC = "ip_src";
	public static final String IP_DST = "ip_dst";
	public static final String TCP_SEQ = "tcp_seq";
	public static final String TCP_ACK = "tcp_ack";
	public static final String TCP_LEN = "tcp_len";
	public static final String TCP_CONNECTION_SYN = "tcp_connection_syn";
	public static final String TCP_CONNECTION_SACK = "tcp_connection_sack";
	public static final String TCP_CONNECTION_RST = "tcp_connection_rst";
	public static final String TCP_CONNECTION_FIN = "tcp_connection_fin";
	public static final String TCP_ANALYSIS_ACKS_FRAME = "tcp_analysis_acks_frame";
	public static final String TCP_ANALYSIS_ACK_RTT = "tcp_analysis_ack_rtt";
	public static final String TCP_ANALYSIS_KEEP_ALIVE = "tcp_analysis_keep_alive";
	public static final String TCP_ANALYSIS_DUPLICATE_ACK = "tcp_analysis_duplicate_ack";
	public static final String TCP_ANALYSIS_RETRANSMISSION = "tcp_analysis_retransmission";
	public static final String TCP_SEGMENT = "tcp_segment";
	public static final String HTTP_REQUEST = "http_request";
	public static final String HTTP_RESPONSE = "http_response";
	public static final String HTTP_REQUEST_IN = "http_request_in";
	public static final String HTTP_RESPONSE_CODE = "http_response_code";

	// 指标，补充的
	public static final String CLIENT_IP = "client_ip";
	public static final String SERVER_IP = "server_ip";
	public static final String TCP = "tcp";
	public static final String TCP_FIRST_SEGMENT = "tcp_first_segment";
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
	public static final String TCP_UP_RETRANS = "tcp_up_retrans";
	public static final String TCP_DOWN_RETRANS = "tcp_down_retrans";
	public static final String TCP_UP_PAYLOAD = "tcp_up_payload";
	public static final String TCP_DOWN_PAYLOAD = "tcp_down_payload";
	public static final String HTTP_REQ_TRANS_DELAY = "http_req_trans_delay";
	public static final String HTTP_RESP_DELAY = "http_resp_delay";
	public static final String HTTP_RESP_TRANS_DELAY = "http_resp_trans_delay";
	public static final String CONNECT_COUNT = "connect_count";

}
