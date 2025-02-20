package com.signify.alexareviews.model;

public enum ResponseCode {

	BAD_REQUEST("Please check the inputs"), AUTHORIZATION_FAILED("Please verify your credential/token"),
	NOT_FOUND("Unable to locate the resource"), SUCCESS("SUCCESS"), FAILED("FAILED"),
	UNAUTHORIZED("Unathurized access"), FORBIDDEN("Forbidden"), NO_LONGER_EXIST("Resource no longer exist");

	private String message;

	private ResponseCode(String message) {
		this.message = message;
	}

	/**
	 * This will be removed in future version Use {@link #message}
	 * 
	 * @return
	 */
	@Deprecated(forRemoval = true)
	public String getMessage() {
		return this.message;
	}

	public String message() {
		return this.message;
	}
}
