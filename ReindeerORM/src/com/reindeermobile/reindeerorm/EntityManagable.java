package com.reindeermobile.reindeerorm;

import com.reindeermobile.reindeerorm.exception.EntityMappingException;

public interface EntityManagable {
	<T> T find(T entity, Class<T> clazz);

	<T> T find(long id, Class<T> clazz);

	<T> void persist(T entity, Class<T> clazz) throws EntityMappingException;

	<T> T merge(T entity, Class<T> clazz) throws EntityMappingException;

	<T> void remove(T entity, Class<T> clazz);

	<T> Query<T> createNamedNativeQuery(String queryName, Class<T> clazz);
}
