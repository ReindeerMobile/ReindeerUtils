package com.reindeermobile.reindeerorm.bootstrep;

import com.reindeermobile.reindeerorm.DatabaseColumn;
import com.reindeermobile.reindeerorm.DatabaseTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Collection;

/**
 * Az adatbázis inicializálását végzi el a "felmeppelt" intitások alapján.
 * @author zsdoma
 *
 */
public class MappedDataBaseHelper extends DataBaseHelper {
	public static final String TAG = "MappedDataBaseHelper";

	protected DatabaseTable databaseTable;
	protected Collection<DatabaseTable> databaseTableList;
	protected boolean debugMode = true;

	public MappedDataBaseHelper(Context context, String name, int version,
			DatabaseTable databaseTable) {
		super(context, name, version);
		this.databaseTable = databaseTable;
		/*
		 * Csak, hogy az onCreate/onUpgrade lefusson. Hack a little bit. :P
		 */
		SQLiteDatabase database = getWritableDatabase();
		database.close();
	}

	public MappedDataBaseHelper(Context context, String name, int version,
			Collection<DatabaseTable> databaseTableList) {
		super(context, name, version);
		this.databaseTableList = databaseTableList;
		/*
		 * Csak, hogy az onCreate/onUpgrade lefusson. Hack a little bit. :P
		 */
		SQLiteDatabase database = getWritableDatabase();
		database.close();
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.d(TAG, "onCreate - START");
		if (this.databaseTableList == null) {
			database.execSQL(generateCreateQuery(this.databaseTable));
		} else {
			for (DatabaseTable databaseTable : this.databaseTableList) {
				database.execSQL(generateCreateQuery(databaseTable));
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
		for (int i = 1; i < this.version + 1; i++) {
			String sqlFile = "insert_v" + i + ".sql";
			Log.i(TAG, "onUpgrade - insert/update tables: " + sqlFile);
			super.loadSqlFile(database, sqlFile);
		}
		//
		// String sqlFile = "insert_v" + dbVersion + ".sql";
		// Log.i(TAG, "onCreate - insert tables: " + sqlFile);
		// loadSqlFile(database, sqlFile);
	}

	@Override
	protected void update(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		/*
		 * if (debugMode) { // Fejlesztői módban minden frissítéskor reseteli a
		 * // táblát. db.execSQL(generateDropQuery(this.databaseTable));
		 * db.execSQL(generateCreateQuery(this.databaseTable)); }
		 */

		/*
		 * EZT MÉG NINCS IMPLEMENTÁLVA. Most még csupán
		 * 
		 * Itt elvileg a a kapott verzióig fel kellene frissíteni az adatbázis
		 * adatait. Fejlesztői módban egy teljesen üres tábla lesz, egyébként
		 * módosítani kellene a meglevőt. Valahogy jelezni kellene vagy
		 * ellenőrizni, hogy a tábla struktúrája változott-e. Ezt meg lehetne
		 * úgy is, hogy nem a kliens dönti el, hanem a szerver a verziójának
		 * megfelelő alter query-t küld, ami lefutás után módosítja az adatokat.
		 */
		/*
		 * Itt csak INSERT vagy UPDATE query-k lesznek.
		 */

		Log.i(TAG,
				"onUpgrade - DataBaseHelper.onUpgrade - v"
						+ database.getVersion() + "(" + oldVersion + ","
						+ newVersion + ")");

		for (int i = oldVersion + 1; i < newVersion + 1; i++) {
			String sqlFile = "insert_v" + newVersion + ".sql";
			Log.i(TAG, "onUpgrade - insert/update tables: " + sqlFile);
			super.loadSqlFile(database, sqlFile);
		}
	}

	protected String generateCreateQuery(DatabaseTable databaseTable) {
		Log.d(TAG, "generateCreateQuery - START");
		int columnCount = 0;
		int maxCount = databaseTable.getAllColumn().size();

		StringBuilder sb = new StringBuilder("CREATE TABLE "
				+ databaseTable.getName());
		sb = sb.append("(").append(
				databaseTable.getPrimaryColumn().getColumnName());
		sb = sb.append(" ").append(
				databaseTable.getPrimaryColumn().getTypeString());
		sb = sb.append(" PRIMARY KEY AUTOINCREMENT");

		if (maxCount > 0) {
			sb = sb.append(",");
		}

		for (DatabaseColumn column : databaseTable.getAllColumn().values()) {
			columnCount++;
			if (!column.isPrimary()) {
				sb = sb.append(column.getColumnName()); // name
				sb = sb.append(" ").append(column.getTypeString()); // type
				if (column.isNotnull()) {
					sb = sb.append(" NOT NULL"); // notnull
				}
				if (columnCount < maxCount) {
					sb = sb.append(",");
				}
			}
		}
		sb = sb.append(")");
		Log.d(TAG, "generateCreateQuery - query - " + sb.toString());
		return sb.toString();
	}

	protected String generateDropQuery(DatabaseTable databaseTable) {
		Log.d(TAG, "generateDropQuery - START");
		StringBuilder sb = new StringBuilder("DROP TABLE IF EXISTS "
				+ databaseTable.getName());
		Log.d(TAG, "generateDropQuery - query - " + sb.toString());
		return sb.toString();
	}
}
