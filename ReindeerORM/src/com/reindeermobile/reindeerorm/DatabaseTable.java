package com.reindeermobile.reindeerorm;

import com.reindeermobile.reindeerorm.annotations.AutoIncrement;
import com.reindeermobile.reindeerorm.annotations.Column;
import com.reindeermobile.reindeerorm.annotations.Id;
import com.reindeermobile.reindeerorm.annotations.NativeNamedQueries;
import com.reindeermobile.reindeerorm.annotations.NativeNamedQuery;
import com.reindeermobile.reindeerorm.annotations.NotNull;
import com.reindeermobile.reindeerorm.annotations.Table;
import com.reindeermobile.reindeerorm.exception.EntityMappingException;

import android.util.Log;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DatabaseTable {
	public static final String TAG = "DatabaseTable";
	public static final String ID_COLUMN_NAME = "_id";

	private String tableName = EntityManagerFactory.TABLE_NAME_PREFIX;
	private String primaryColumnName;
	private Map<String, DatabaseColumn> columnMap;
	private Map<String, String> nativeNamedQueriesMap;

	public <T> DatabaseTable(Class<T> clazz) {
		if (clazz.isAnnotationPresent(Table.class)) {
			this.tableName += (clazz.getAnnotation(Table.class)).name();
			if (this.tableName.length() == 0) {
				this.tableName = clazz.getName();
			}

			this.nativeNamedQueriesMap = resolvNativeNamedQueries(clazz);
		} else {
			throw new AnnotationFormatError("Table annotation missing!");
		}
		this.columnMap = this.resolveAnnotatedFields(clazz);
	}

	public DatabaseTable(String tableName) {
		this.tableName = tableName;
		this.columnMap = new HashMap<String, DatabaseColumn>();
	}

	public String toCreateQuery() {
		int columnCount = 0;
		int maxCount = getAllColumn().size();

		StringBuilder builder = new StringBuilder("CREATE TABLE " + getName());
		builder = builder.append("(");

		getPrimaryColumn().toCreateQueryFragment(builder);
		if (maxCount > 0) {
			builder = builder.append(",");
		}

		for (DatabaseColumn column : getAllColumn().values()) {
			columnCount++;
			if (!column.isPrimary()) {
				column.toCreateQueryFragment(builder);
				if (columnCount < maxCount) {
					builder = builder.append(",");
				}
			}
		}

		builder = builder.append(")");
		return builder.toString();
	}

	public List<String> toAlterQueries(DatabaseTable oldDatabaseTable)
			throws EntityMappingException {
		List<String> alterQueryStringList = new ArrayList<String>();

		Log.d(TAG, "toAlterQueries - " + oldDatabaseTable.toString());

		for (DatabaseColumn databaseColumn : getAllColumn().values()) {
			boolean exists = oldDatabaseTable.hasColumn(databaseColumn
					.getColumnName());
			if (!exists) {
				// TODO toAlterQuery - check column
				if (!databaseColumn.isPrimary()) {
					StringBuilder builder = new StringBuilder("ALTER TABLE "
							+ getName() + " ADD COLUMN ");
					databaseColumn.toCreateQueryFragment(builder);
					alterQueryStringList.add(builder.toString());
				}
			} else if (exists
					&& oldDatabaseTable
							.getColumn(databaseColumn.getColumnName())
							.getTypeName().equals(databaseColumn.getTypeName())) {
				Log.i(TAG, "toAlterQueries - same column type, ignored: "
						+ databaseColumn.getColumnName());
			} else {
				throw new EntityMappingException("Column already exists! "
						+ databaseColumn.getColumnName());
			}
		}

		return alterQueryStringList;
	}

	public String toDropQuery() {
		return "DROP TABLE " + getName();
	}

	public String getName() {
		return this.tableName;
	}

	public final DatabaseColumn getPrimaryColumn() {
		return this.getColumn(this.primaryColumnName);
	}

	public final void setPrimaryColumn(DatabaseColumn primaryColumn) {
		this.primaryColumnName = primaryColumn.getColumnName();
	}

	public void addColumn(DatabaseColumn column) {
		this.columnMap.put(column.getColumnName(), column);
	}

	public DatabaseColumn getColumn(String columnName) {
		return this.columnMap.get(columnName);
	}

	public boolean hasColumn(String columnName) {
		return this.columnMap.containsKey(columnName);
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
				+ ", primaryColumnName=" + this.primaryColumnName
				+ ", columnMap=" + this.columnMap + "]";
	}

	private <T> Map<String, String> resolvNativeNamedQueries(Class<T> clazz) {
		Map<String, String> nativeNamedQueriesMap = new HashMap<String, String>();
		if (clazz.isAnnotationPresent(NativeNamedQueries.class)) {
			NativeNamedQueries nativeNamedQueries = clazz
					.getAnnotation(NativeNamedQueries.class);
			for (NativeNamedQuery nativeNamedQuery : nativeNamedQueries.value()) {
				nativeNamedQueriesMap.put(nativeNamedQuery.name(),
						nativeNamedQuery.query());
			}
		}
		return nativeNamedQueriesMap;
	}

	// TODO refactor
	private <T> Map<String, DatabaseColumn> resolveAnnotatedFields(
			Class<? super T> clazz) {
		Map<String, DatabaseColumn> columnMap = new HashMap<String, DatabaseColumn>();
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

				String methodNamePostfix = field.getName().substring(0, 1)
						.toUpperCase()
						+ field.getName().substring(1);
				String getterMethodName = (field.getType() != boolean.class && field
						.getType() != Boolean.class) ? "get"
						+ methodNamePostfix : "is" + methodNamePostfix;
				String setterMethodName = "set" + methodNamePostfix;

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

					columnMap.put(databaseColumn.getColumnName(),
							databaseColumn);
				} catch (SecurityException exception) {
					exception.printStackTrace();
				} catch (NoSuchMethodException exception) {
					exception.printStackTrace();
				}
			}
		}
		Log.i(TAG, "resolveAnnotatedFields - OK - " + clazz);
		return columnMap;
	}

}
