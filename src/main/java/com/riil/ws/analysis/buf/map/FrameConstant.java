package com.riil.ws.analysis.buf.map;

public final class FrameConstant {
	// 值
	public static final Integer ICMP_PROTO_NUM = 1;
	public static final Integer TCP_PROTO_NUM = 6;
	public static final Integer UDP_PROTO_NUM = 17;
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
	public static final String UDP_STREAM = "udp_stream";
	public static final String UDP_SRCPORT = "udp_srcport";
	public static final String UDP_DSTPORT = "udp_dstport";
	public static final String DNS_FLAGS_RESPONSE = "dns_flags_response";
	public static final int DNS_MESSAGE_IS_A_QUERY = 0;
	public static final int DNS_MESSAGE_IS_A_RESPONSE = 1;
	public static final String DNS_QRY_TYPE = "dns_qry_type";
	public static final int DNS_QRY_TYPE_HOST = 1;
	public static final String DNS_FLAGS_RCODE = "dns_flags_rcode";
	public static final int DNS_FLAGS_RCODE_NO_ERROR = 0;
	public static final String DNS_QRY_NAME = "dns_qry_name";
	public static final String DNS_COUNT_ANSWERS = "dns_count_answers";
	public static final String DNS_RESP_TYPE = "dns_resp_type";
	public static final int DNS_RESP_TYPE_HOST_ADDRESS = 1;
	public static final String DNS_A = "dns_a";

	// 指标，补充的
	public static final String CLIENT_IP = "client_ip";
	public static final String SERVER_IP = "server_ip";
	public static final String SERVER_PORT = "server_port";
	public static final String ONLINE_USER = "online_user";
	public static final String TCP = "tcp";
	public static final String TCP_FIRST_SEGMENT = "tcp_first_segment";
	public static final String TCP_CLIENT_CONNECTION_RST = "tcp_client_connection_rst";
	public static final String TCP_SERVER_CONNECTION_RST = "tcp_server_connection_rst";
	public static final String TCP_CLIENT_CONNECTION_NO_RESP = "tcp_client_connection_no_resp";
	public static final String TCP_SERVER_CONNECTION_NO_RESP = "tcp_server_connection_no_resp";
	public static final String TCP_CONNECTION_SUCCESS = "tcp_connection_success";
	public static final String TCP_DISCONNECTION_NORMAL = "tcp_disconnection_normal";
	public static final String TCP_CLIENT_DISCONNECTION_RST = "tcp_client_disconnection_rst";
	public static final String TCP_SERVER_DISCONNECTION_RST = "tcp_server_disconnection_rst";
	public static final String TCP_CLIENT_DISCONNECTION_FIN_RST = "tcp_client_disconnection_fin_rst";
	public static final String TCP_SERVER_DISCONNECTION_FIN_RST = "tcp_server_disconnection_fin_rst";
	public static final String TCP_CLIENT_DISCONNECTION_FIN_NO_RESP = "tcp_client_disconnection_fin_no_resp";
	public static final String TCP_SERVER_DISCONNECTION_FIN_NO_RESP = "tcp_server_disconnection_fin_no_resp";
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
	public static final String REQ_COUNT = "req_count";
	public static final String DNS_QRY_HOST = "dns_qry_host";
	public static final String DNS_ANSWER_IP = "dns_answer_ip";
	public static final String DNS_REPLY_DELAY = "dns_reply_delay";
	public static final String DNS_QRY_SUCCESS = "dns_qry_success";
	public static final String DNS_ERROR_ANSWER = "dns_error_answer";
	public static final String DNS_NO_RESPONSE = "dns_no_response";
	public static final String DNS_SERVER_RESP_NO_IP = "dns_server_resp_no_ip";

}
