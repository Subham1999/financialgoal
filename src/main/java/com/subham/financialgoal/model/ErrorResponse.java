package com.subham.financialgoal.model;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {
	
	private String username;
	private Map<ApiErrors, String> errors;
	
	public ErrorResponse() {
		errors = new HashMap<>();
	}
	
	public ErrorResponse username(String username) {
		this.username = username;
		return this;
	}
	
	public ErrorResponse addError(Map.Entry<ApiErrors, String> error) {
		errors.putIfAbsent(error.getKey(), error.getValue());
		return this;
	}
	
	public ErrorResponse addError(boolean expression, Map.Entry<ApiErrors, String> error) {
		if (expression) {
			errors.putIfAbsent(error.getKey(), error.getValue());
			return this;
		}
		return this;
	}
	
	public Map<String, ?> get() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", username);
		map.put("errors", errors);
		return map;
	}
}
