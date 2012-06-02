package com.reindeermobile.reindeerorm.bootstrep;


import com.reindeermobile.reindeerorm.BaseDbAdaptor;
import com.reindeermobile.reindeerorm.BaseDbEntity;
import com.reindeermobile.reindeerorm.DatabaseColumn;
import com.reindeermobile.reindeerorm.DatabaseTable;
import com.reindeermobile.reindeerorm.DbListFilter;
import com.reindeermobile.reindeerorm.IDatabaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

@Deprecated
public class DbAdaptor<T> extends BaseDbAdaptor implements
		IDatabaseAdapter<T> {
	private Class<T> clazz;
	private DatabaseTable databaseTable;

	public DbAdaptor(Class<T> clazz, Context context, String databaseName,
			int dbVersion, DatabaseTable databaseTable) {
		super(new MappedDataBaseHelper(context, databaseName, dbVersion,
				databaseTable));
		this.clazz = clazz;
		this.databaseTable = databaseTable;
	}

	@Override
	public T find(T entity) {
		SQLiteDatabase database = null;
		Cursor cursor = null;
		try {
			database = getDatabase();

			String query = this.buildSelectQueryByEntity(entity).toString();
			// Log.d(TAG, "find - query: " + query);
			cursor = database.rawQuery(query, null);
			entity = parseCursor(entity, cursor);
		} catch (SQLException exception) {
			Log.w(TAG, "list - sql exception", exception);
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Exception exception) {
					Log.w(TAG, "list - cursor close exception");
				}
			}
			if (database != null) {
				try {
					database.close();
				} catch (Exception exception) {
					Log.w(TAG, "list - database close exception");
				}
			}
		}
		return entity;
	}

	@Override
	public T find(long id) {
		T entity = null;
		SQLiteDatabase database = null;
		Cursor cursor = null;
		try {
			database = getDatabase();

			String query = this.buildSelectQueryByEntity(id).toString();
			// Log.d(TAG, "findById - query: " + query);
			cursor = database.rawQuery(query, null);

			entity = parseCursor(cursor);
		} catch (SQLException exception) {
			Log.w(TAG, "list - sql exception", exception);
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Exception exception) {
					Log.w(TAG, "list - cursor close exception");
				}
			}
			if (database != null) {
				try {
					database.close();
				} catch (Exception exception) {
					Log.w(TAG, "list - database close exception");
				}
			}
		}
		Log.d(TAG, "findById - " + entity);
		return entity;
	}

	@Deprecated
	@Override
	public T findById(long id) {
		return find(id);
	}

	@Override
	public T insert(T entity) {
		// Log.d(TAG, "insert - START");

		ContentValues values = this.entityToContentValues(entity);

		SQLiteDatabase database = null;
		long newId = -1;
		try {
			database = getDatabase();
			newId = database.insert(this.databaseTable.getName(), null,
					values);

			if (newId != -1) {
				Method setter = this.databaseTable.getIdColumn()
						.getSetter();
				setter.invoke(entity, newId);
				// entity.setId(newId);
			} else {
				entity = null;
			}
		} catch (SQLException exception) {
			Log.w(TAG, "insert - Exception: ", exception);
			throw exception;
		} catch (IllegalArgumentException exception) {
			exception.printStackTrace();
		} catch (IllegalAccessException exception) {
			exception.printStackTrace();
		} catch (InvocationTargetException exception) {
			exception.printStackTrace();
		} finally {
			if (database != null) {
				try {
					database.close();
				} catch (Exception exception) {
					Log.w(TAG, "insert - close databse :", exception);
				}
			}
		}
		return entity;
	}

	public T replace(T entity) {
		// Log.d(TAG, "replace - START");

		ContentValues values = this.entityToContentValues(entity);

		SQLiteDatabase database = null;
		long newId = -1;
		try {
			database = getDatabase();
			newId = database.replace(this.databaseTable.getName(), null,
					values);
			// newId = database.insert(this.databaseTable.getName(), null,
			// values);

			if (newId != -1) {
				/*
				 * Ki kell kérni az ID setterjét és beállítani az új ID-t.
				 */
				Method setter = this.databaseTable.getIdColumn()
						.getSetter();
				setter.invoke(entity, newId);
				// entity.setId(newId);
			} else {
				entity = null;
			}
		} catch (SQLException exception) {
			Log.w(TAG, "replace - Exception: ", exception);
			throw exception;
		} catch (IllegalArgumentException exception) {
			Log.w(TAG, "replace - Exception: ", exception);
			throw exception;
		} catch (IllegalAccessException exception) {
			Log.w(TAG, "replace - Exception: ", exception);
			// TODO Elnyeli a kivételt!
			// throw exception;
		} catch (InvocationTargetException exception) {
			Log.w(TAG, "replace - Exception: ", exception);
			// TODO Elnyeli a kivételt!
			// throw exception;
		} finally {
			if (database != null) {
				try {
					database.close();
				} catch (Exception exception) {
					Log.w(TAG, "replace - close databse :", exception);
				}
			}
		}
		return entity;
	}

	@Override
	public int insertList(List<T> entities) {
		Log.d(TAG, "insertList - START");
		Method getter = this.databaseTable.getIdColumn().getGetter();
		int count = 0;
		for (T entity : entities) {
			Log.d(TAG, "insertList - persist: " + entity.toString());
			Long id = null;
			try {
				id = (Long) getter.invoke(entity, new Object[] {});
				T existed = null;
				T savedEntity = null;
				if (id != null) {
					existed = findById(id);
				}
				if (existed != null) {
					savedEntity = update(entity);
					Log.d(TAG, "insertList - updated: " + entity.toString());
				} else {
					savedEntity = insert(entity);
					Log.d(TAG,
							"insertList - persisted: " + entity.toString());
				}
				if (savedEntity != null) {
					count++;
				}
			} catch (IllegalArgumentException exception) {
				exception.printStackTrace();
			} catch (IllegalAccessException exception) {
				exception.printStackTrace();
			} catch (InvocationTargetException exception) {
				exception.printStackTrace();
			}
		}
		return count;
	}

	public T update(T entity) {
		ContentValues values = this.entityToContentValues(entity);
		SQLiteDatabase database = null;
		long newId = -1;
		try {
			database = getDatabase();

			Method getter = this.databaseTable.getIdColumn().getGetter();
			Method setter = this.databaseTable.getIdColumn().getSetter();

			Long id = (Long) getter.invoke(entity, new Object[] {});

			newId = database.update(this.databaseTable.getName(), values,
					COLUMN_ID + " = " + id, null);

			setter.invoke(entity, newId);
			// entity.setId(newId);
		} catch (SQLException exception) {
			Log.w(TAG, "update - Exception: ", exception);
			throw exception;
		} catch (IllegalArgumentException exception) {
			exception.printStackTrace();
		} catch (IllegalAccessException exception) {
			exception.printStackTrace();
		} catch (InvocationTargetException exception) {
			exception.printStackTrace();
		} finally {
			if (database != null) {
				try {
					database.close();
				} catch (Exception exception) {
					Log.w(TAG, "update - close databse :", exception);
				}
			}
		}
		return entity;
	};

	/**
	 * Ha az entity null, akkor törli az összes elemet.
	 * 
	 * @see {@link #clear()}
	 */
	@Override
	public int remove(T entity) {
		SQLiteDatabase database = null;
		int affectedCount = 0;
		try {
			database = getDatabase();

			Method getter = this.databaseTable.getIdColumn().getGetter();

			if (entity != null) {
				Long id = (Long) getter.invoke(entity, new Object[] {});
				affectedCount = database.delete(
						this.databaseTable.getName(),
						COLUMN_ID + "= " + id, null);
			} else {
				affectedCount = database.delete(
						this.databaseTable.getName(), "1", null);
			}
		} catch (SQLException exception) {
			Log.w(TAG, "remove - Exception:", exception);
			throw exception;
		} catch (IllegalArgumentException exception) {
			exception.printStackTrace();
		} catch (IllegalAccessException exception) {
			exception.printStackTrace();
		} catch (InvocationTargetException exception) {
			exception.printStackTrace();
		} finally {
			if (database != null) {
				try {
					database.close();
				} catch (Exception exception) {
					Log.w(TAG, "remove - close database:", exception);
				}
			}
		}
		return affectedCount;
	}

	/**
	 * Clear all rows.
	 * 
	 * @see #remove(BaseDbEntity)
	 */
	@Override
	public int clear() {
		return this.remove(null);
	}

	public List<T> listWithQuery(String query) {
		List<T> resultList = null;
		SQLiteDatabase database = null;
		Cursor cursor = null;
		try {
			database = getDatabase();

			Log.d(TAG, "listWithQuery - query: " + query);
			cursor = database.rawQuery(query, null);

			resultList = parseCursorToList(cursor);
		} catch (SQLException exception) {
			Log.w(TAG, "listWithQuery - sql exception", exception);
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Exception exception) {
					Log.w(TAG, "listWithQuery - cursor close exception");
				}
			}
			if (database != null) {
				try {
					database.close();
				} catch (Exception exception) {
					Log.w(TAG, "listWithQuery - database close exception");
				}
			}
		}
		return resultList;
	}

	@Override
	public List<T> list() {
		String query = this.buildSelectQuery().toString();
		return listWithQuery(query);
	}

	@Override
	public List<T> list(String rowWhereClause) {
		String query = this.buildSelectQuery(rowWhereClause).toString();
		return listWithQuery(query);
	}

	@Override
	public List<T> list(DbListFilter filter) {
		throw new UnsupportedOperationException();
	}

	// TODO remove warnings
	// TODO messing logs from catch blocks
	@SuppressWarnings("unchecked")
	@Override
	public List<T> parseCursorToList(Cursor cursor) {
		List<T> resultList = null;
		try {
			resultList = (ArrayList<T>) ArrayList.class.newInstance();
		} catch (IllegalAccessException exception1) {
			//FIXME kivétel naplózás
			exception1.printStackTrace();
		} catch (InstantiationException exception1) {
			//FIXME kivétel naplózás
			exception1.printStackTrace();
		}

		if (resultList != null && cursor != null && cursor.moveToFirst()) {
			// Log.d(TAG, "parseCursorToList - HERE");
			do {
				try {
					T result = this.clazz.newInstance();
					for (String columnName : cursor.getColumnNames()) {
						Type columnType = this.databaseTable.getColumn(
								columnName).getType();
						Method setter = this.databaseTable.getColumn(
								columnName).getSetter();
						Object value = DbAdaptor
								.getCursorColumnByType(cursor, columnName,
										columnType);
						setter.invoke(result, value);
					}
					resultList.add(result);
				} catch (SecurityException exception) {
					exception.printStackTrace();
				} catch (IllegalArgumentException exception) {
					exception.printStackTrace();
				} catch (IllegalAccessException exception) {
					exception.printStackTrace();
				} catch (InstantiationException exception) {
					exception.printStackTrace();
				} catch (InvocationTargetException exception) {
					exception.printStackTrace();
				}
			} while (cursor.moveToNext());
		}

		return resultList;
	}

	@Override
	public T parseCursor(Cursor cursor) {
		return parseCursor(null, cursor);
	}

	@Override
	public T parseCursor(T entity, Cursor cursor) {
		if (cursor != null && cursor.moveToFirst()) {
			if (entity == null) {
				try {
					entity = this.clazz.newInstance();
				} catch (IllegalAccessException exception) {
					Log.w(TAG, "parseCursor - IllegalAccessException",
							exception);
				} catch (InstantiationException exception) {
					Log.w(TAG, "parseCursor - InstantiationException",
							exception);
				}
			}
			for (String columnName : cursor.getColumnNames()) {
				Type columnType = this.databaseTable.getColumn(columnName)
						.getType();
				Method setter = this.databaseTable.getColumn(columnName)
						.getSetter();
				Object value = DbAdaptor.getCursorColumnByType(
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

	private StringBuilder buildSelectQuery() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder = stringBuilder.append("SELECT * ");
		stringBuilder = stringBuilder.append("FROM ").append(
				this.databaseTable.getName());
		return stringBuilder;
	}

	private StringBuilder buildSelectQuery(String whereClause) {
		StringBuilder builder = buildSelectQuery();
		builder = builder.append(" WHERE ").append(whereClause);
		return builder;
	}

	private StringBuilder buildSelectQueryByEntity(long id) {
		return buildSelectQuery("_id=" + id);
	}

	private StringBuilder buildSelectQueryByEntity(T entity) {
		// return buildSelectQuery("_id=" + entity.getId());
		return buildSelectQuery(buildWhereClauseByEntity(entity).toString());
	}

	private StringBuilder buildWhereClauseByEntity(T entity) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (DatabaseColumn column : this.databaseTable.getAllColumn()
				.values()) {
			Object obj = null;
			try {
				obj = column.getGetter().invoke(entity, new Object[] {});
			} catch (IllegalArgumentException exception) {
				exception.printStackTrace();
			} catch (IllegalAccessException exception) {
				exception.printStackTrace();
			} catch (InvocationTargetException exception) {
				exception.printStackTrace();
			}
			if (obj != null) {
				if (!first) {
					builder = builder.append(" AND ");
				} else {
					first = false;
				}
				builder = builder.append(column.getColumnName())
						.append("=");
				String value = DbAdaptor.objToString(obj);
				if (obj instanceof String) {
					value = "'" + value + "'";
				}
				builder = builder.append(value);
			}
		}
		return builder;
	}

	/**
	 * A kapott entity-t egy {@link ContentValues} példánnyá alakítja. Ha
	 * egy adattagja null, akkor azt figyelmen kívűl hagyja vagyis nem kerül
	 * bele a kimenetbe.
	 * 
	 * @param entity
	 * @return Az entity adattagjait tartalmazó {@link ContentValues}.
	 */
	private ContentValues entityToContentValues(T entity) {
		ContentValues values = new ContentValues();
		for (Entry<String, DatabaseColumn> entry : this.databaseTable
				.getAllColumn().entrySet()) {
			/*
			 * Ha AutoIncrement, akkor nem teszi be az oszlopot. TODO
			 * AutoIncrement ellenőrzés: ezt még gondold át!
			 */
			if (!entry.getValue().isAutoIncrement()) {
				Method getter = entry.getValue().getGetter();
				try {
					Object value = getter.invoke(entity, new Object[] {});
					String valueString = DbAdaptor.objToString(value);
					if (valueString != null) {
						values.put(entry.getValue().getColumnName(),
								valueString);
					}
				} catch (IllegalArgumentException exception) {
					Log.w(TAG, "insert - IllegalArgumentException"
							+ exception);
				} catch (IllegalAccessException exception) {
					Log.w(TAG, "insert - IllegalAccessException"
							+ exception);
				} catch (InvocationTargetException exception) {
					Log.w(TAG, "insert - InvocationTargetException"
							+ exception);
				}
			}
		}
		return values;
	}

	public static String objToString(Object object) {
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

	public static Object getCursorColumnByType(Cursor cursor,
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

}