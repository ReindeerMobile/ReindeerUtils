package com.reindeermobile.reindeerutils.mvp.exceptions;

public class HandlerNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 5368393013791707140L;

	public HandlerNotFoundException() {
		super();
	}

	public HandlerNotFoundException(int what) {
		super(String.valueOf(what));
	}

	public HandlerNotFoundException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public HandlerNotFoundException(String detailMessage) {
		super(detailMessage);
	}

	public HandlerNotFoundException(Throwable throwable) {
		super(throwable);
	}

}
