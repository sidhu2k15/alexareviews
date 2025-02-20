package com.signify.alexareviews.model;

public class Response<T> {

	private boolean success;
	private String code;
	private String message;
	private T data;

	public Response() {
	}

	public Response(boolean success, String code, String message) {
		this(success, code, message, null);
	}

	public Response(boolean success, String code, String message, T data) {
		this.success = success;
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
