package com.reindeermobile.reindeerorm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Az adatbázis inicializálását végzi el a "felmeppelt" intitások alapján.
 * 
 * @author zsdoma
 * 
 */
class MappedDataBaseHelper extends DataBaseHelper {
	public static final String TAG = "MappedDataBaseHelper";

	protected Collection<DatabaseTable> databaseTableList;
	protected boolean debugMode = true;

	public MappedDataBaseHelper(Context context, String name, int version,
			DatabaseTable databaseTable) {
		super(context, name, version);
		this.databaseTableList = new ArrayList<DatabaseTable>();
		this.databaseTableList.add(databaseTable);

		this.initDatabase();
	}

	public MappedDataBaseHelper(Context context, String name, int version,
			Collection<DatabaseTable> databaseTableList) {
		super(context, name, version);
		this.databaseTableList = databaseTableList;
		this.initDatabase();
	}

	private void initDatabase() {
		/*
		 * Csak, hogy az onCreate/onUpgrade lefusson. Hack a little bit. :P
		 */
		SQLiteDatabase database = getWritableDatabase();
		database.close();
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.d(TAG, "onCreate - START");
		if (this.databaseTableList != null) {
			for (DatabaseTable databaseTable : this.databaseTableList) {
				database.execSQL(databaseTable.toCreateQuery());
			}
		}
		this.init(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.d(TAG, "onUpgrade - START");
		update(database, oldVersion, newVersion);
	}

	@Override
	protected void init(SQLiteDatabase database) {
		String sqlFile = "import.sql";
		Log.i(TAG, "onCreate - import tables: " + sqlFile);
		super.loadSqlFile(database, sqlFile);
	}

	@Override
	protected void update(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.i(TAG, "update - v" + database.getVersion() + "(" + oldVersion
				+ "," + newVersion + ")");

		String sqlFile = "upgrade.sql";
		Log.i(TAG, "onUpgrade - upgrade tables: " + sqlFile);
		super.loadSqlFile(database, sqlFile);
	}

	protected String generateDropQuery(DatabaseTable databaseTable) {
		Log.d(TAG, "generateDropQuery - START");
		StringBuilder sb = new StringBuilder("DROP TABLE IF EXISTS "
				+ databaseTable.getName());
		Log.d(TAG, "generateDropQuery - query - " + sb.toString());
		return sb.toString();
	}
}
