package com.reindeermobile.reindeerorm.bootstrep;

import com.reindeermobile.reindeerorm.DatabaseTable;
import com.reindeermobile.reindeerorm.IDatabaseAdapter;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public enum DbAdapterFactory {
	INSTANCE;

	public static final String TAG = "DbAdapterFactory";

	private Map<Class<?>, DatabaseTable> databaseTableMap;
	private boolean firstRun = true;

	DbAdapterFactory() {
	}

	public void init(Class<?>... classes) {
		Log.i(TAG, "init - START");
		this.databaseTableMap = new HashMap<Class<?>, DatabaseTable>();
		for (Class<?> clazz : classes) {
			DatabaseTable databaseTable = new DatabaseTable(clazz);
			this.databaseTableMap.put(clazz, databaseTable);
		}
	}

	public static <T> IDatabaseAdapter<T> createInstance(Class<T> clazz,
			Context context, String databaseName, int dbVersion) {

		if (DbAdapterFactory.INSTANCE.databaseTableMap == null) {
			throw new NullPointerException(
					"Run the DbAdaptorFactory.init() before create an instance.");
		}

		if (DbAdapterFactory.INSTANCE.firstRun) {
			DbAdapterFactory.INSTANCE.firstRun = false;
			new MappedDataBaseHelper(context,
					databaseName, dbVersion,
					DbAdapterFactory.INSTANCE.databaseTableMap.values());
		}

		return new DbAdaptor<T>(clazz, context,
				databaseName, dbVersion,
				DbAdapterFactory.INSTANCE.databaseTableMap.get(clazz));
	}

}
