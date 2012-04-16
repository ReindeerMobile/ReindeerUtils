package com.reindeermobile.reindeerutils.db;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class DatabaseColumn {
	private String columnName;
	private Type type;
	private Method setter;
	private Method getter;
	private boolean notnull;
	private boolean primary;

	private static Map<Type, String> typeMap;

	static {
		typeMap = new HashMap<Type, String>();
		typeMap.put(String.class, "TEXT");
		typeMap.put(Integer.class, "INTEGER");
		typeMap.put(int.class, "INTEGER");
		typeMap.put(Double.class, "REAL");
		typeMap.put(double.class, "REAL");
		typeMap.put(Long.class, "INTEGER");
		typeMap.put(long.class, "INTEGER");
		typeMap.put(Boolean.class, "INTEGER");
		typeMap.put(boolean.class, "INTEGER");
		typeMap.put(Date.class, "INTEGER");
	}

	DatabaseColumn(String columnName, Type type, Method setter, Method getter) {
		super();
		this.columnName = columnName;
		this.type = type;
		this.setter = setter;
		this.getter = getter;
	}

	DatabaseColumn(String columnName, Type columnType, Method setter,
			Method getter, boolean notnull, boolean primary) {
		this(columnName, columnType, setter, getter);
		this.notnull = notnull;
		this.primary = primary;
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

	@Override
	public String toString() {
		return "DatabaseColumn [columnName=" + this.columnName + ", type="
				+ this.type + ", setter=" + this.setter + "]";
	}
}
