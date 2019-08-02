package com.riil.ws.analysis.process;

@FunctionalInterface
public interface CosumerCheckedException<T> {
	void accept(T t) throws Exception;
}
