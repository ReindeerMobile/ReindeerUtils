package com.reindeermobile.reindeerorm;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * EntityManager gyártó singleton.
 * 
 * @author zsdoma
 * 
 */
public enum EntityManagerFactory {
	INSTANCE;

	public static final String TAG = "EntityManagerFactory";

	private static final String PROPERTY_DATABASE_NAME = "databaseName";
	private static final String PROPERTY_DATABASE_VERSION = "databaseVersion";
	private static final String PROPERTY_TABLE_NAME_PREFIX = "tableNamePrefix";
	private static final String DEFAULT_DATABASE_NAME = "database";
	private static final int DEFAULT_DATABASE_VERSION = 1;

	public static String TABLE_NAME_PREFIX = "";

	private Context context;
	private String databaseName;
	private int version;

	private Map<Class<?>, DatabaseTable> databaseTableMap;

	public static void init(Context context, Class<?> classes) {
		init(context, loadProperties(context), classes);
	}

	public static void init(Context context, Properties properties,
			Class<?>... classes) {
		// if (!properties.contains(PROPERTY_DATABASE_NAME)
		// || (properties.getProperty(PROPERTY_DATABASE_NAME) == null)
		// || properties.getProperty(PROPERTY_DATABASE_NAME).length() == 0) {
		// properties.setProperty(PROPERTY_DATABASE_NAME,
		// DEFAULT_DATABASE_NAME);
		// }
		//
		// if (!properties.contains(PROPERTY_DATABASE_VERSION)
		// || (properties.getProperty(PROPERTY_DATABASE_VERSION) == null)
		// || properties.getProperty(PROPERTY_DATABASE_VERSION).length() == 0) {
		// properties.setProperty(PROPERTY_DATABASE_VERSION,
		// DEFAULT_DATABASE_VERSION + "");
		// }

		String databaseName = properties.getProperty(PROPERTY_DATABASE_NAME,
				DEFAULT_DATABASE_NAME);
		int databaseVersion = Integer.valueOf(properties.getProperty(
				PROPERTY_DATABASE_VERSION, DEFAULT_DATABASE_VERSION + ""));
		TABLE_NAME_PREFIX = properties.getProperty(PROPERTY_TABLE_NAME_PREFIX,
				TABLE_NAME_PREFIX);

		INSTANCE.init(context, databaseName, databaseVersion, classes);
	}

	public static EntityManagable createInstance() {
		if (EntityManagerFactory.INSTANCE.databaseTableMap == null) {
			throw new NullPointerException(
					"Run the DbAdaptorFactory.init() before create an instance.");
		}
		return new EntityManager(EntityManagerFactory.INSTANCE.context,
				EntityManagerFactory.INSTANCE.databaseName,
				EntityManagerFactory.INSTANCE.version);
	}

	public <T> DatabaseTable getDatabaseTable(Class<T> clazz) {
		return databaseTableMap.get(clazz);
	}

	private void init(Context context, String databaseName, int dbVersion,
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
		new MappedDataBaseHelper(context, databaseName, dbVersion,
				EntityManagerFactory.INSTANCE.databaseTableMap.values());
	}

	private static Properties loadProperties(Context context) {
		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = context.getAssets().open("database.properties");
			properties.load(inputStream);
		} catch (IOException exception) {
			Log.w(TAG, "init - ", exception);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException exception) {
					Log.w(TAG, "init - ", exception);
				}
			}
		}
		return properties;
	}
}
