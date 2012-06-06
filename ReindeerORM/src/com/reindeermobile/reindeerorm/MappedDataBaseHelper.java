package com.reindeermobile.reindeerorm;

import com.reindeermobile.reindeerorm.exception.EntityMappingException;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Az adatbázis inicializálását végzi el a "felmeppelt" intitások alapján.
 * 
 * @author zsdoma
 * 
 */
class MappedDataBaseHelper extends DataBaseHelper {
	public static final String TAG = "MappedDataBaseHelper";

	private static final Set<String> IGNORED_TABLES;

	protected Collection<DatabaseTable> databaseTableList;
	protected boolean debugMode = true;

	static {
		IGNORED_TABLES = new HashSet<String>();
		IGNORED_TABLES.add("android_metadata");
		IGNORED_TABLES.add("sqlite_sequence");
	}

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
		getWritableDatabase().close();
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
//		Log.d(TAG, "onCreate - START");
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
//		Log.d(TAG, "onUpgrade - START");

		Map<String, DatabaseTable> oldTablesMap = this
				.loadExistsTables(database);

		for (DatabaseTable databaseTable : this.databaseTableList) {
			try {
				if (oldTablesMap.containsKey(databaseTable.getName())) {
					List<String> alterQueries = databaseTable
							.toAlterQueries(oldTablesMap.get(databaseTable
									.getName()));
					for (String alterQuery : alterQueries) {
//						Log.d(TAG, "onUpgrade - alterQuery: " + alterQuery);
						database.execSQL(alterQuery);
					}
					oldTablesMap.remove(databaseTable.getName());
				}
			} catch (EntityMappingException exception) {
				Log.e(TAG, "onUpgrade - ", exception);
			}
		}

		// drop unnesasery tables
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

	private Map<String, DatabaseTable> loadExistsTables(SQLiteDatabase database) {
		List<String> tableNameList = new LinkedList<String>();
		Map<String, DatabaseTable> existTableMap = new HashMap<String, DatabaseTable>();
		String query = "SELECT name FROM sqlite_master";
		Cursor cursor = null;

		try {
			cursor = database.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					String tableName = cursor.getString(cursor
							.getColumnIndex("name"));
					if (tableName != null
							&& !IGNORED_TABLES.contains(tableName)) {
						tableNameList.add(tableName);
					}
				} while (cursor.moveToNext());
			}
		} catch (SQLException exception) {
			Log.w(TAG, "loadExistsTables - ", exception);
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Exception exception) {
					Log.w(TAG, "loadExistsTables - ", exception);
				}
			}
		}

		for (String tableName : tableNameList) {
			DatabaseTable databaseTable = this.loadTableInfo(database,
					tableName);
			if (databaseTable != null) {
				existTableMap.put(databaseTable.getName(), databaseTable);
			}
		}

		return existTableMap;
	}

	private DatabaseTable loadTableInfo(SQLiteDatabase database,
			String tableName) {
		DatabaseTable databaseTable = new DatabaseTable(tableName);
		String query = "pragma table_info(" + tableName + ")";
		Cursor cursor = null;

		try {
			cursor = database.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					String columnName = cursor.getString(1);
					String typeName = cursor.getString(2);
					// Ha kulcsmező, akkor nem szedi fel.
					int key = cursor.getInt(5);
//					Log.d(TAG,
//							"loadTableInfo - "
//									+ String.format("%s %s %d", columnName,
//											typeName, key));
					if (columnName != null && typeName != null && key == 0) {
						databaseTable.addColumn(new DatabaseColumn(columnName,
								typeName));
					}
				} while (cursor.moveToNext());
			}
		} catch (SQLException exception) {
			Log.w(TAG, "loadTableInfo - ", exception);
		} finally {
			try {
				if (cursor != null) {
					cursor.close();
				}
			} catch (Exception exception) {
				Log.w(TAG, "loadTableInfo - ", exception);
			}
		}

		return databaseTable;
	}

}
