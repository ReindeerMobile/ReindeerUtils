package com.reindeermobile.reindeerorm;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class DatabaseColumn {
	private static final String STRING_NOT_NULL = "NOT NULL";

	public static final String TAG = "DatabaseColumn";

	private static final String STRING_AUTOINCREMENT = "AUTOINCREMENT";
	private static final String STRING_PRIMARY_KEY = "PRIMARY KEY";

	private String columnName;
	private String typeName;
	private Type type;
	private Method setter;
	private Method getter;
	private boolean notnull;
	private boolean primary;
	private boolean autoIncrement;

	public static Map<Type, String> typeMap;

	static {
		typeMap = new HashMap<Type, String>();
		typeMap.put(String.class, "TEXT");
		typeMap.put(Integer.class, "INTEGER");
		typeMap.put(int.class, "INTEGER");
		typeMap.put(Double.class, "REAL");
		typeMap.put(double.class, "REAL");
		typeMap.put(Long.class, "INTEGER");
		typeMap.put(long.class, "INTEGER");
		typeMap.put(Float.class, "REAL");
		typeMap.put(float.class, "REAL");
		typeMap.put(Boolean.class, "INTEGER");
		typeMap.put(boolean.class, "INTEGER");
		typeMap.put(Date.class, "INTEGER");
	}

	public DatabaseColumn(String columnName, String typeName) {
		super();
		this.columnName = columnName;
		this.typeName = typeName;
	}

	public DatabaseColumn(String columnName, Type type, Method setter,
			Method getter) {
		this(columnName, typeMap.get(type));
		this.type = type;
		this.setter = setter;
		this.getter = getter;
	}

	public DatabaseColumn(String columnName, Type columnType, Method setter,
			Method getter, boolean notnull, boolean primary,
			boolean autoIncrement) {
		this(columnName, columnType, setter, getter);
		this.notnull = notnull;
		this.primary = primary;
		this.autoIncrement = autoIncrement;
	}

	public StringBuilder toCreateQueryFragment(StringBuilder builder) {
		builder = builder.append(getColumnName());
		builder = builder.append(" " + getTypeString());
		if (isPrimary()) {
			builder = builder.append(" " + STRING_PRIMARY_KEY);
		}
		if (isAutoIncrement()) {
			builder = builder.append(" " + STRING_AUTOINCREMENT);
		}
		if (isNotnull()) {
			builder = builder.append(" " + STRING_NOT_NULL);
		}
		return builder;
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

	public final String getTypeString() {
		return typeMap.get(this.type);
	}

	public boolean isNotnull() {
		return notnull;
	}

	public void setNotnull(boolean notnull) {
		this.notnull = notnull;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public String toString() {
		return "DatabaseColumn [columnName=" + this.columnName + ", type="
				+ this.type + ", setter=" + this.setter + "]";
	}
}
