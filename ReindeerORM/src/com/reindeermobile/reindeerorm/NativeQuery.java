package com.reindeermobile.reindeerorm;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class NativeQuery<T> implements Query<T> {
	public static final String TAG = "NativeQuery";

	private SQLiteDatabase database;
	private Map<String, Object> parameters;
	private String queryString;

	private CursorParser<T> cursorParser;
	private Cursor resultCursor;

	public NativeQuery(SQLiteDatabase database, String queryString) {
		super();
		this.database = database;
		this.queryString = queryString;
	}

	public NativeQuery(String queryString, SQLiteDatabase database,
			Class<T> clazz) {
		super();
		this.database = database;
		this.queryString = queryString;
		this.parameters = new HashMap<String, Object>();
		this.cursorParser = new CursorParser<T>(clazz);
	}

	@Override
	public List<T> list() {
		this.resultCursor = this.query();
		List<T> resultList = this.cursorParser
				.parseCursorToList(this.resultCursor);
		if (this.resultCursor != null) {
			this.resultCursor.close();
		}
		return resultList;
	}

	@Override
	public T uniqueEntity() {
		this.resultCursor = this.query();
		T result = this.cursorParser.parseCursor(this.resultCursor);
		if (this.resultCursor != null) {
			this.resultCursor.close();
		}
		return result;
	}

	@Override
	public void execute() {
		this.database.execSQL(buildQuery());
	}

	@Override
	public void setParameter(String name, Object value) {
		this.parameters.put(name, value);
	}

	@Override
	public Object getParameter(String name) {
		return this.parameters.get(name);
	}

	private Cursor query() {
		return this.database.rawQuery(this.buildQuery(), null);
	}

	private String buildQuery() {
		String buildedQuery = String.valueOf(this.queryString);
		for (Entry<String, Object> param : this.parameters.entrySet()) {
			buildedQuery.replaceFirst(param.getKey(),
					String.valueOf(param.getValue()));
		}
		return buildedQuery;
	}

}
