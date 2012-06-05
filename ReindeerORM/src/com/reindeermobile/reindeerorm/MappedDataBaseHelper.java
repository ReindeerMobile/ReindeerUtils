package com.reindeermobile.reindeerorm;

import com.reindeermobile.reindeerorm.exception.EntityMappingException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		// TODO onUpgrade: fetch exist tables
		Map<String, DatabaseTable> oldTablesMap = new HashMap<String, DatabaseTable>();

		for (DatabaseTable databaseTable : this.databaseTableList) {
			try {
				if (oldTablesMap.containsKey(databaseTable.getName())) {
					List<String> alterQueries = databaseTable
							.toAlterQuery(oldTablesMap.get(databaseTable
									.getName()));
					for (String alterQuery : alterQueries) {
						database.rawQuery(alterQuery, null);
					}
					oldTablesMap.remove(databaseTable.getName());
				}
			} catch (EntityMappingException exception) {
				Log.e(TAG, "onUpgrade - ", exception);
			}
		}

		// TODO drop unnesasery tables
		for (DatabaseTable databaseTable : oldTablesMap.values()) {
			database.rawQuery(databaseTable.toDropQuery(), null);
		}
		
		// Load new datas.
		this.update(database, oldVersion, newVersion);
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

}
