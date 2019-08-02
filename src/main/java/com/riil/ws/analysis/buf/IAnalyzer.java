package com.riil.ws.analysis.buf;

public interface IAnalyzer {
	void save(String indexLineJson, String frameLineJson) throws Exception ;

	void analysis() throws Exception;

	void output() throws Exception;
}
