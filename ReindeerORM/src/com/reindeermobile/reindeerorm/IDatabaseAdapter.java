package com.reindeermobile.reindeerorm;

import android.database.Cursor;

import java.util.List;

public interface IDatabaseAdapter<T> {
	T find(T entity);

	T find(long id);
	
	@Deprecated
	T findById(long id);

	T insert(T entity);
	
	T replace(T entity);
	
	T update(T entity);
	
	int insertList(List<T> entities);

	int remove(T entity);
	
	int clear();

	List<T> list();
	
	List<T> list(String rowWhereClause);

	List<T> list(DbListFilter filter);

	List<T> parseCursorToList(Cursor cursor);

	T parseCursor(Cursor cursor);

	T parseCursor(T entity, Cursor cursor);
}
