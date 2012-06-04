package com.reindeermobile.reindeerorm;

import com.reindeermobile.reindeerorm.exception.EntityMappingException;

import android.content.ContentValues;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map.Entry;

class DatabaseEntity<T> {
	public static final String TAG = "DatabaseEntity";
	
	private T entity;
	private DatabaseTable databaseTable;

	public DatabaseEntity(T entity, DatabaseTable databaseTable) {
		super();
		this.entity = entity;
		this.databaseTable = databaseTable;
	}
	
	public Object getValue(String columnName) throws EntityMappingException {
		Method getter = databaseTable.getAllColumn().get(columnName).getGetter();
		Object obj = null;
		try {
			obj = getter.invoke(entity, new Object[] {});
		} catch (IllegalArgumentException exception) {
			Log.w(TAG, "getValue - ", exception);
			throw new EntityMappingException(exception);
		} catch (IllegalAccessException exception) {
			Log.w(TAG, "getValue - ", exception);
			throw new EntityMappingException(exception);
		} catch (InvocationTargetException exception) {
			Log.w(TAG, "getValue - ", exception);
			throw new EntityMappingException(exception);
		}
		return obj;
	}
	
	public void setValue(String columnName, Object value) throws EntityMappingException {
		Method setter = databaseTable.getAllColumn().get(columnName).getSetter();
		try {
			setter.invoke(entity, new Object [] {value});
		} catch (IllegalArgumentException exception) {
			Log.w(TAG, "setValue - ", exception);
			throw new EntityMappingException(exception);
		} catch (IllegalAccessException exception) {
			Log.w(TAG, "setValue - ", exception);
			throw new EntityMappingException(exception);
		} catch (InvocationTargetException exception) {
			Log.w(TAG, "setValue - ", exception);
			throw new EntityMappingException(exception);
		}
	}
	
	public T getEntity() {
		return this.entity;
	} 
	
	public ContentValues contentValues() {
		ContentValues values = new ContentValues();
		for (Entry<String, DatabaseColumn> columnEntry : databaseTable
				.getAllColumn().entrySet()) {
			/*
			 * Ha AutoIncrement, akkor nem teszi be az oszlopot. TODO
			 * AutoIncrement ellenőrzés: ezt még gondold át!
			 */
			if (!columnEntry.getValue().isAutoIncrement()) {
				Method getter = columnEntry.getValue().getGetter();
				try {
					Object value = getter.invoke(entity, new Object[] {});
					String valueString = objToString(value);
					if (valueString != null) {
						values.put(columnEntry.getValue().getColumnName(),
								valueString);
					}
				} catch (IllegalArgumentException exception) {
					Log.w(TAG, "contentValues - ", exception);
				} catch (IllegalAccessException exception) {
					Log.w(TAG, "contentValues - ", exception);
				} catch (InvocationTargetException exception) {
					Log.w(TAG, "contentValues - ", exception);
				}
			}
		}
		return values;
	}

	private static String objToString(Object object) {
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
	
}
