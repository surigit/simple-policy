package com.mbr.platform.policy.ex;

public class PolicyLoadException extends Exception {

	public PolicyLoadException() {
		super();
	}

	public PolicyLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PolicyLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public PolicyLoadException(String message) {
		super(message);
	}

	public PolicyLoadException(Throwable cause) {
		super(cause);
	}
}
