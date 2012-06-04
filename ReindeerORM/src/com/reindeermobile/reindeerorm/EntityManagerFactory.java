package com.reindeermobile.reindeerorm;


import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * EntityManager gyártó singleton.
 * 
 * @author zsdoma
 * 
 */
public enum EntityManagerFactory {
	INSTANCE;

	public static final String TAG = "EntityManagerFactory";

	private Context context;
	private String databaseName;
	private int version;

	private Map<Class<?>, DatabaseTable> databaseTableMap;

	// private boolean firstRun = true;

	/**
	 * Inicializálja a gyártó enumot a megadott entity (Table annotációval
	 * annotált) osztályokkal.
	 * 
	 * @param classes
	 */
	public void init(Context context, String databaseName, int dbVersion,
			Class<?>... classes) {
		Log.i(TAG, "init - START");
		this.databaseTableMap = new HashMap<Class<?>, DatabaseTable>();
		for (Class<?> clazz : classes) {
			DatabaseTable databaseTable = new DatabaseTable(clazz);
			this.databaseTableMap.put(clazz, databaseTable);
		}

		this.databaseName = databaseName;
		this.version = dbVersion;
		this.context = context;
		// EntityManagerFactory.INSTANCE.firstRun = false;
		new MappedDataBaseHelper(context, databaseName, dbVersion,
				EntityManagerFactory.INSTANCE.databaseTableMap.values());
	}

	public static EntityManagable createInstance() {
		if (EntityManagerFactory.INSTANCE.databaseTableMap == null) {
			throw new NullPointerException(
					"Run the DbAdaptorFactory.init() before create an instance.");
		}
		/*
		 * Ha még nem indult el, akkor itt gyakorlatilag példányosítja az
		 * adatbázist. Az feltérképezett entitások alapján létrehozza a
		 * táblákat.
		 */
		// if (EntityManagerFactory.INSTANCE.firstRun) {
		// EntityManagerFactory.INSTANCE.firstRun = false;
		// new MappedDataBaseHelper(context, databaseName, dbVersion,
		// EntityManagerFactory.INSTANCE.databaseTableMap.values());
		// }

		/*
		 * Visszatér a T alapján példányosított EntityManager-el.
		 */
		return new EntityManager(EntityManagerFactory.INSTANCE.context,
				EntityManagerFactory.INSTANCE.databaseName,
				EntityManagerFactory.INSTANCE.version);
	}

	public <T> DatabaseTable getDatabaseTable(Class<T> clazz) {
		return databaseTableMap.get(clazz);
	}

}
