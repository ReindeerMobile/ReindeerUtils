package com.reindeermobile.reindeerorm;

import com.reindeermobile.reindeerorm.annotations.AutoIncrement;
import com.reindeermobile.reindeerorm.annotations.Column;
import com.reindeermobile.reindeerorm.annotations.Id;
import com.reindeermobile.reindeerorm.annotations.NativeNamedQueries;
import com.reindeermobile.reindeerorm.annotations.NativeNamedQuery;
import com.reindeermobile.reindeerorm.annotations.NotNull;
import com.reindeermobile.reindeerorm.annotations.Table;

import android.util.Log;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

class DatabaseTable {
	public static final String TAG = "DatabaseTable";
	public static final String ID_COLUMN_NAME = "_id";

	private String tableName;
	private DatabaseColumn primaryColumn;
	private Map<String, DatabaseColumn> columnMap;
	private Map<String, String> nativeNamedQueriesMap;

	public <T> DatabaseTable(Class<T> clazz) {
		if (clazz.isAnnotationPresent(Table.class)) {
			this.tableName = (clazz.getAnnotation(Table.class)).name();
			if (this.tableName.length() == 0) {
				this.tableName = clazz.getName();
			}

			if (clazz.isAnnotationPresent(NativeNamedQueries.class)) {
				nativeNamedQueriesMap = new HashMap<String, String>();
				NativeNamedQueries nativeNamedQueries = clazz
						.getAnnotation(NativeNamedQueries.class);
				for (NativeNamedQuery nativeNamedQuery : nativeNamedQueries
						.value()) {
					this.nativeNamedQueriesMap.put(nativeNamedQuery.name(),
							nativeNamedQuery.query());
				}

			}
		} else {
			throw new AnnotationFormatError("Table annotation missing!");
		}
		this.columnMap = new HashMap<String, DatabaseColumn>();
		this.resolveAnnotatedFields(clazz);
	}

	public String getName() {
		return this.tableName;
	}

	public final DatabaseColumn getPrimaryColumn() {
		return this.primaryColumn;
	}

	public final void setPrimaryColumn(DatabaseColumn primaryColumn) {
		this.primaryColumn = primaryColumn;
	}

	public void addColumn(DatabaseColumn column) {
		this.columnMap.put(column.getColumnName(), column);
	}

	public DatabaseColumn getColumn(String columnName) {
		return this.columnMap.get(columnName);
	}

	public DatabaseColumn getIdColumn() {
		return this.columnMap.get(ID_COLUMN_NAME);
	}

	public Map<String, DatabaseColumn> getAllColumn() {
		return this.columnMap;
	}

	public String getNativeQuery(String name) {
		return this.nativeNamedQueriesMap.get(name);
	}

	@Override
	public String toString() {
		return "DatabaseTable [tableName=" + this.tableName
				+ ", primaryColumn=" + this.primaryColumn + ", columnMap="
				+ this.columnMap + "]";
	}

	// TODO refactor
	private <T> void resolveAnnotatedFields(Class<? super T> clazz) {
		if (clazz.getSuperclass() != null) {
			resolveAnnotatedFields(clazz.getSuperclass());
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
				// Log.d(TAG, "resolveAnnotatedFields - columnName: "
				// + columnName);

				String methodNamePostfix = field.getName().substring(0, 1)
						.toUpperCase()
						+ field.getName().substring(1);
				String getterMethodName = (field.getType() != boolean.class && field
						.getType() != Boolean.class) ? "get"
						+ methodNamePostfix : "is" + methodNamePostfix;
				String setterMethodName = "set" + methodNamePostfix;

				// Log.d(TAG, "resolveAnnotatedFields - : " +
				// getterMethodName
				// + "," + setterMethodName);
				//
				// Log.d(TAG,
				// "resolveAnnotatedFields - clazz: "
				// + clazz.getName());
				// Log.d(TAG, "resolveAnnotatedFields - columnType: "
				// + columnType);
				try {
					Method setter = clazz.getMethod(setterMethodName,
							field.getType());
					Method getter = clazz.getMethod(getterMethodName);

					// check not null annotation
					DatabaseColumn databaseColumn = new DatabaseColumn(
							columnName, columnType, setter, getter,
							field.isAnnotationPresent(NotNull.class),
							field.isAnnotationPresent(Id.class),
							field.isAnnotationPresent(AutoIncrement.class));

					// check primary key annotation
					if (field.isAnnotationPresent(Id.class)) {
						this.setPrimaryColumn(databaseColumn);
					}
					this.addColumn(databaseColumn);

					// Log.d(TAG, "resolveAnnotatedFields - databaseColumn: "
					// + databaseColumn);
				} catch (SecurityException exception) {
					exception.printStackTrace();
				} catch (NoSuchMethodException exception) {
					exception.printStackTrace();
				}
			}
		}
		Log.i(TAG, "resolveAnnotatedFields - OK - " + clazz);
	}

}
