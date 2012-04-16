package com.reindeermobile.reindeerutils.mvp;

public class ServiceNotRegisteredException extends Exception {
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
