package com.reindeermobile.reindeerutils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BaseDbAdaptor {
    protected static final String LOG_TAG_DATABASE = "database";
    
    public static final String COLUMN_ID = "_id";
    
	private static final String DROP_TABLES_SQL = "drop_tables.sql";
	private static final String CREATE_TABLES_SQL = "create_tables.sql";
	private static final String INSERT_SQL = "insert.sql";
	private static final String ALTER_SQL = "alter_tables.sql";
	private DataBaseHelper dataBaseHelper;
	private SQLiteDatabase database;

	protected BaseDbAdaptor(final Context context, final String databaseName, int dbVersion) {
		super();
		Log.d(LOG_TAG_DATABASE, "Initialize DB adaptor");
		SqlResource sqlResource = new SqlResource(DROP_TABLES_SQL, CREATE_TABLES_SQL, INSERT_SQL, ALTER_SQL);
		setDataBaseHelper(new DataBaseHelper(context, databaseName, sqlResource, dbVersion));
		setDatabase(getDataBaseHelper().getWritableDatabase());
	}

	public final DataBaseHelper getDataBaseHelper() {
		return dataBaseHelper;
	}

	public final void setDataBaseHelper(final DataBaseHelper dataBaseHelper) {
		this.dataBaseHelper = dataBaseHelper;
	}

	public final SQLiteDatabase getDatabase() {
		return database;
	}

	public final void setDatabase(final SQLiteDatabase database) {
		this.database = database;
	}
}
