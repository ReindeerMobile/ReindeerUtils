package com.reindeermobile.reindeerutils.mvp.exceptions;

public class ServiceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 5479218705574674489L;

	public ServiceNotFoundException() {
		super();
	}

	public ServiceNotFoundException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ServiceNotFoundException(String detailMessage) {
		super(detailMessage);
	}

	public ServiceNotFoundException(Throwable throwable) {
		super(throwable);
	}

}
