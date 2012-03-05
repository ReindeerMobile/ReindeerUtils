package com.reindeermobile.reindeerutils.db;

import com.reindeermobile.reindeerutils.view.StringUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public enum DbAdapterFactory {
	INSTANCE;

	DbAdapterFactory() {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE })
	public @interface Table {
		String name() default StringUtils.EMPTY_STRING;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public @interface Column {
		String name() default StringUtils.EMPTY_STRING;

		int textViewId() default -1;
	}

	public static <T extends BaseDbEntity> IDatabaseAdapter<T> createInstance(
			Class<T> clazz, Context context, String databaseName, int dbVersion) {
		return DbAdapterFactory.INSTANCE.new DbAdaptor<T>(clazz, context,
				databaseName, dbVersion);
	}

	public class DbAdaptor<T extends BaseDbEntity> extends BaseDbAdaptor
			implements IDatabaseAdapter<T> {
		private Class<T> clazz;
		private DatabaseTable databaseTable;

		public DbAdaptor(Class<T> clazz, Context context, String databaseName,
				int dbVersion) {
			super(context, databaseName, dbVersion);
			this.clazz = clazz;

			String tableName = null;
			if (clazz.isAnnotationPresent(Table.class)) {
				tableName = (clazz.getAnnotation(Table.class)).name();
				if (tableName.length() == 0) {
					tableName = clazz.getName();
				}
				this.databaseTable = new DatabaseTable(tableName);
			} else {
				throw new AnnotationFormatError("Table annotation missing!");
			}
			this.resolveAnnotatedFields(this.clazz, databaseTable);
		}

		@Override
		public T find(T entity) {
			SQLiteDatabase database = null;
			Cursor cursor = null;
			try {
				database = getDatabase();

				String query = this.buildSelectQueryByEntity(entity).toString();
				Log.d(TAG, "find - query: " + query);
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
		public T findById(long id) {
			T entity = null;
			SQLiteDatabase database = null;
			Cursor cursor = null;
			try {
				database = getDatabase();

				String query = this.buildSelectQueryByEntity(id).toString();
				Log.d(TAG, "findById - query: " + query);
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
			return entity;
		}

		@Override
		public T insert(T entity) {
			Log.d(TAG, "save - START");

			ContentValues values = this.entityToContentValues(entity);

			SQLiteDatabase database = null;
			long newId = -1;
			try {
				database = getDatabase();
				newId = database.insert(this.databaseTable.getName(), null,
						values);
				entity.setId(newId);
			} catch (SQLException exception) {
				Log.w(TAG, "save - Exception: ", exception);
				throw exception;
			} finally {
				if (database != null) {
					try {
						database.close();
					} catch (Exception exception) {
						Log.w(TAG, "save - close databse :", exception);
					}
				}
			}
			return entity;
		}

		@Override
		public T insertList(List<T> entities) {
			throw new UnsupportedOperationException();
		}

		public T update(T entity) {
			ContentValues values = this.entityToContentValues(entity);
			SQLiteDatabase database = null;
			long newId = -1;
			try {
				database = getDatabase();
				newId = database.update(this.databaseTable.getName(), values,
						COLUMN_ID + " = " + entity.getId(), null);
				entity.setId(newId);
			} catch (SQLException exception) {
				Log.w(TAG, "update - Exception: ", exception);
				throw exception;
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

		@Override
		public void remove(T entity) {
			SQLiteDatabase database = null;
			try {
				database = getDatabase();
				database.delete(this.databaseTable.getName(), COLUMN_ID + "= "
						+ entity.getId(), null);
			} catch (SQLException exception) {
				Log.w(TAG, "delete - Exception:", exception);
				throw exception;
			} finally {
				if (database != null) {
					try {
						database.close();
					} catch (Exception exception) {
						Log.w(TAG, "delete - close database:", exception);
					}
				}
			}
		}

		@Override
		public List<T> list() {
			List<T> resultList = null;
			SQLiteDatabase database = null;
			Cursor cursor = null;
			try {
				database = getDatabase();

				String query = this.buildSelectQuery().toString();
				Log.d(TAG, "list - query: " + query);
				cursor = database.rawQuery(query, null);

				resultList = parseCursorToList(cursor);
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
			return resultList;
		}

		@Override
		public List<T> list(DbAdapterFilter filter) {
			throw new UnsupportedOperationException();
		}

		// TODO remove warnings
		// TODO messing logs from catch blocks
		@Override
		public List<T> parseCursorToList(Cursor cursor) {
			List<T> resultList = null;
			try {
				resultList = (ArrayList<T>) ArrayList.class.newInstance();
			} catch (IllegalAccessException exception1) {
				exception1.printStackTrace();
			} catch (InstantiationException exception1) {
				exception1.printStackTrace();
			}

			if (cursor != null && cursor.moveToFirst()) {
				do {
					try {
						T result = this.clazz.newInstance();
						for (String columnName : cursor.getColumnNames()) {
//							Log.d(TAG, "parseCursorToList - columnName: "
//									+ columnName);
							Type columnType = this.databaseTable.getColumn(
									columnName).getType();
							Method setter = this.databaseTable.getColumn(
									columnName).getSetter();
							Object value = DbAdapterFactory
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
			if (cursor != null && cursor.moveToFirst()) {
				for (String columnName : cursor.getColumnNames()) {
					Type columnType = this.databaseTable.getColumn(columnName)
							.getType();
					Method setter = this.databaseTable.getColumn(columnName)
							.getSetter();
					Object value = DbAdapterFactory.getCursorColumnByType(
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

		private StringBuilder buildSelectQueryByEntity(long id) {
			StringBuilder builder = buildSelectQuery();
			builder = builder.append(" WHERE _id = ").append(id);
			return builder;
		}

		private StringBuilder buildSelectQueryByEntity(T entity) {
			StringBuilder builder = buildSelectQuery();
			builder = builder.append(" WHERE _id=").append(entity.getId());
			return builder;
		}

		private ContentValues entityToContentValues(T entity) {
			ContentValues values = new ContentValues();
			for (Entry<String, DatabaseColumn> entry : this.databaseTable
					.getAllColumn().entrySet()) {
				Method getter = entry.getValue().getGetter();
				try {
					Object value = getter.invoke(entity, new Object[] {});
					values.put(entry.getValue().getColumnName(),
							objToString(value));
				} catch (IllegalArgumentException exception) {
					Log.w(TAG, "insert - IllegalArgumentException" + exception);
				} catch (IllegalAccessException exception) {
					Log.w(TAG, "insert - IllegalAccessException" + exception);
				} catch (InvocationTargetException exception) {
					Log.w(TAG, "insert - InvocationTargetException" + exception);
				}
			}
			return values;
		}

		private DatabaseTable resolveAnnotatedFields(Class<? super T> clazz,
				DatabaseTable databaseTable) {
			if (clazz != BaseDbEntity.class) {
				databaseTable = resolveAnnotatedFields(clazz.getSuperclass(),
						databaseTable);
			}

			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				if (field.isAnnotationPresent(Column.class)) {
					Column column = field.getAnnotation(Column.class);

					Type columnType = field.getType();
					String columnName = column.name();
					if (columnName.length() == 0) {
						columnName = field.getName();
					}
//					Log.d(TAG, "resolveAnnotatedFields - columnName: "
//							+ columnName);

					String methodNamePostfix = field.getName().substring(0, 1)
							.toUpperCase()
							+ field.getName().substring(1);
					String getterMethodName = (field.getType() != boolean.class && field
							.getType() != Boolean.class) ? "get"
							+ methodNamePostfix : "is" + methodNamePostfix;
					String setterMethodName = "set" + methodNamePostfix;

//					Log.d(TAG, "resolveAnnotatedFields - : " + getterMethodName
//							+ "," + setterMethodName);
//
//					Log.d(TAG,
//							"resolveAnnotatedFields - clazz: "
//									+ clazz.getName());
//					Log.d(TAG, "resolveAnnotatedFields - columnType: "
//							+ columnType);
					try {
						Method setter = clazz.getMethod(setterMethodName,
								field.getType());
						Method getter = clazz.getMethod(getterMethodName);
						DatabaseColumn databaseColumn = new DatabaseColumn(
								columnName, columnType, setter, getter);
						databaseTable.addColumn(new DatabaseColumn(columnName,
								columnType, setter, getter));
//						Log.d(TAG, "resolveAnnotatedFields - databaseColumn: "
//								+ databaseColumn);
					} catch (SecurityException exception) {
						exception.printStackTrace();
					} catch (NoSuchMethodException exception) {
						exception.printStackTrace();
					}
				}
			}
			Log.i(TAG, "resolveAnnotatedFields - OK - " + clazz);
			return databaseTable;
		}

		private class DatabaseTable {
			private String tableName;
			private Map<String, DatabaseColumn> columnMap;

			public DatabaseTable(String tableName) {
				super();
				this.tableName = tableName;
				this.columnMap = new HashMap<String, DatabaseColumn>();
			}

			public String getName() {
				return this.tableName;
			}

			public void addColumn(DatabaseColumn column) {
				this.columnMap.put(column.columnName, column);
			}

			public DatabaseColumn getColumn(String columnName) {
				return this.columnMap.get(columnName);
			}

			public Map<String, DatabaseColumn> getAllColumn() {
				return this.columnMap;
			}
		}

		private class DatabaseColumn {
			private String columnName;
			private Type type;
			private Method setter;
			private Method getter;

			public DatabaseColumn(String columnName, Type type, Method setter,
					Method getter) {
				super();
				this.columnName = columnName;
				this.type = type;
				this.setter = setter;
				this.getter = getter;
			}

			public final String getColumnName() {
				return this.columnName;
			}

			public final Type getType() {
				return this.type;
			}

			public final Method getSetter() {
				return this.setter;
			}

			public final Method getGetter() {
				return this.getter;
			}

			@Override
			public String toString() {
				return "DatabaseColumn [columnName=" + this.columnName
						+ ", type=" + this.type + ", setter=" + this.setter
						+ "]";
			}
		}
	}

	private static Object getCursorColumnByType(Cursor cursor,
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
		} else if (type == boolean.class) {
			value = (cursor.getInt(cursor.getColumnIndex(columnName)) > 0) ? true
					: false;
		}
		return value;
	}

	private static String objToString(Object object) {
		if (object instanceof String) {
			return (String) object;
		} else if (object instanceof Long || object.getClass() == long.class) {
			return ((Long) object).toString();
		} else if (object instanceof Integer || object.getClass() == int.class) {
			return ((Integer) object).toString();
		} else if (object instanceof Boolean
				|| object.getClass() == boolean.class) {
			return ((Boolean) object).toString();
		} else if (object instanceof Double
				|| object.getClass() == double.class) {
			return ((Double) object).toString();
		} else {
			return object.toString();
		}
	}

}
