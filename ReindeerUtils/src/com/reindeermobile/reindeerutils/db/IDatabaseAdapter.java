package com.reindeermobile.reindeerutils.db;

import android.database.Cursor;

import java.util.List;

public interface IDatabaseAdapter<T extends BaseDbEntity> {
	T find(T entity);

	T findById(long id);

	T insert(T entity);
	
	T update(T entity);
	
	T insertList(List<T> entities);

	void remove(T entity);

	List<T> list();
	
	List<T> list(String rowWhereClause);

	List<T> list(DbListFilter filter);

	List<T> parseCursorToList(Cursor cursor);

	T parseCursor(Cursor cursor);

	T parseCursor(T entity, Cursor cursor);
}
