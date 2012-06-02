package com.reindeermobile.reindeerorm.entitymanager;

import com.reindeermobile.reindeerorm.BaseDbAdaptor;
import com.reindeermobile.reindeerorm.DatabaseTable;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Date;

public abstract class AbstractEntityManager {
	public static final String TAG = "AbstractEntityManager";
	
	protected BaseDbAdaptor baseDbAdaptor;

	public AbstractEntityManager(Context context, String databaseName,
			int dbVersion) {
		super();
		this.baseDbAdaptor = new BaseDbAdaptor(context, databaseName, dbVersion);
	}
	
	static String objToString(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof String) {
			return (String) object;
		} else if (object instanceof Long || object.getClass() == long.class) {
			return ((Long) object).toString();
		} else if (object instanceof Float || object.getClass() == float.class) {
			return ((Float) object).toString();
		} else if (object instanceof Integer || object.getClass() == int.class) {
			return ((Integer) object).toString();
		} else if (object instanceof Boolean
				|| object.getClass() == boolean.class) {
			return ((Boolean) object) ? "1" : "0";
		} else if (object instanceof Double
				|| object.getClass() == double.class) {
			return ((Double) object).toString();
		} else if (object instanceof Date) {
			return String.valueOf(((Date) object).getTime());
		} else {
			return object.toString();
		}
	}

	static Object getCursorColumnByType(Cursor cursor,
			String columnName, Type type) {
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
	
	public <T> T parseCursor(Cursor cursor, Class<T> clazz, DatabaseTable databaseTable) {
		return parseCursor(null, cursor, clazz, databaseTable);
	}

	public <T> T parseCursor(T entity, Cursor cursor, Class<T> clazz, DatabaseTable databaseTable) {
		if (cursor != null && cursor.moveToFirst()) {
			if (entity == null) {
				try {
					entity = clazz.newInstance();
				} catch (IllegalAccessException exception) {
					Log.w(TAG, "parseCursor - IllegalAccessException",
							exception);
				} catch (InstantiationException exception) {
					Log.w(TAG, "parseCursor - InstantiationException",
							exception);
				}
			}
			for (String columnName : cursor.getColumnNames()) {
				Type columnType = databaseTable.getColumn(columnName)
						.getType();
				Method setter = databaseTable.getColumn(columnName)
						.getSetter();
				Object value = getCursorColumnByType(
						cursor, columnName, columnType);
				try {
					setter.invoke(entity, value);
				} catch (IllegalArgumentException exception) {
					Log.w(TAG, "parseCursor - IllegalArgumentException",
							exception);
				} catch (IllegalAccessException exception) {
					Log.w(TAG, "parseCursor - IllegalAccessException",
							exception);
				} catch (InvocationTargetException exception) {
					Log.w(TAG, "parseCursor - InvocationTargetException",
							exception);
				}
			}
		}

		return entity;
	}

}
