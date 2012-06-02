package com.reindeermobile.reindeerorm.entitymanager;

public interface EntityManagable {
	<T> T find(T entity, Class<T> clazz);
	<T> T find(long id, Class<T> clazz);
}
