package com.reindeermobile.reindeerorm;


import android.database.Cursor;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class CursorParser<T> {
	public static final String TAG = "CursorParser";

	private Class<T> clazz;
	private DatabaseTable databaseTable;

	public CursorParser(Class<T> clazz) {
		super();
		this.clazz = clazz;
		this.databaseTable = EntityManagerFactory.INSTANCE
				.getDatabaseTable(clazz);
	}

	public T parseCursor(Cursor cursor) {
		T result = createEntityInstance();
		if (result != null) {
			result = parseRow(result, cursor);
		}
		return result;
	}

	public T parseCursor(T entity, Cursor cursor) {
		return this.parseRow(entity, cursor);
	}

	public List<T> parseCursorToList(Cursor cursor) {
		List<T> resultList = new ArrayList<T>();
		if (cursor != null && cursor.moveToFirst()) {
			do {
				T result = createEntityInstance();
				if (result == null) {
					break;
				}
				result = this.parseRow(result, cursor);
				resultList.add(result);
			} while (cursor.moveToNext());
		}
		return resultList;
	}

	private T createEntityInstance() {
		T result = null;
		try {
			result = clazz.newInstance();
		} catch (IllegalAccessException exception) {
			Log.w(TAG, "createEntityInstance - ", exception);
		} catch (InstantiationException exception) {
			Log.w(TAG, "createEntityInstance - ", exception);
		}
		return result;
	}

	private T parseRow(T entity, Cursor cursor) {
		for (String columnName : cursor.getColumnNames()) {
			try {
				Type columnType = databaseTable.getColumn(columnName).getType();
				Method setter = databaseTable.getColumn(columnName).getSetter();
				Object value = CursorParser.getCursorColumnByType(cursor,
						columnName, columnType);
				setter.invoke(entity, value);
			} catch (IllegalArgumentException exception) {
				Log.w(TAG, "parseRow - ", exception);
			} catch (IllegalAccessException exception) {
				Log.w(TAG, "parseRow - ", exception);
			} catch (InvocationTargetException exception) {
				Log.w(TAG, "parseRow - ", exception);
			}
		}
		return entity;
	}

	static Object getCursorColumnByType(Cursor cursor, String columnName,
			Type type) {
		Object value = null;
		if (type == String.class) {
			value = cursor.getString(cursor.getColumnIndex(columnName));
		} else if (type == int.class) {
			value = cursor.getInt(cursor.getColumnIndex(columnName));
		} else if (type == Integer.class) {
			value = cursor.getInt(cursor.getColumnIndex(columnName));
		} else if (type == long.class) {
			value = cursor.getLong(cursor.getColumnIndex(columnName));
		} else if (type == Long.class) {
			value = cursor.getLong(cursor.getColumnIndex(columnName));
		} else if (type == float.class) {
			value = cursor.getFloat(cursor.getColumnIndex(columnName));
		} else if (type == Float.class) {
			value = cursor.getFloat(cursor.getColumnIndex(columnName));
		} else if (type == boolean.class) {
			value = (cursor.getInt(cursor.getColumnIndex(columnName)) > 0) ? true
					: false;
		} else if (type == Date.class) {
			long epoch = (cursor.getLong(cursor.getColumnIndex(columnName)));
			value = new Date(epoch);
		}
		return value;
	}
}
