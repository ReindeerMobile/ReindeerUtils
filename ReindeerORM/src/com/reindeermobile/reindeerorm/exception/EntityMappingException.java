package com.reindeermobile.reindeerorm.exception;

public class EntityMappingException extends Exception {
	private static final long serialVersionUID = 3040999140940663098L;

	public EntityMappingException() {
	}

	public EntityMappingException(String detailMessage) {
		super(detailMessage);
	}

	public EntityMappingException(Throwable throwable) {
		super(throwable);
	}

	public EntityMappingException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
