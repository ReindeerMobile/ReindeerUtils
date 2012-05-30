package com.reindeermobile.reindeerutils.mvp.exceptions;

public class ServiceNotRegisteredException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ServiceNotRegisteredException() {
		super();
	}

	public ServiceNotRegisteredException(String detailMessage,
			Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ServiceNotRegisteredException(String detailMessage) {
		super(detailMessage);
	}

	public ServiceNotRegisteredException(Throwable throwable) {
		super(throwable);
	}

	
}
