package com.reindeermobile.reindeerutils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Collection;

public class MappedDataBaseHelper extends DataBaseHelper {
	public static final String TAG = "MappedDataBaseHelper";
	private DatabaseTable databaseTable;
	private Collection<DatabaseTable> databaseTableList;
	private boolean debugMode = true;

	public MappedDataBaseHelper(Context context, String name, int version,
			DatabaseTable databaseTable) {
		super(context, name, version);
		this.databaseTable = databaseTable;
	}

	public MappedDataBaseHelper(Context context, String name, int version,
			Collection<DatabaseTable> databaseTableList) {
		super(context, name, version);
		this.databaseTableList = databaseTableList;
	}

	@Override
	public final void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate - START");
		if (this.databaseTableList == null) {
			db.execSQL(generateCreateQuery(this.databaseTable));
		} else {
			for (DatabaseTable databaseTable : this.databaseTableList) {
				db.execSQL(generateCreateQuery(databaseTable));
			}
		}

		int dbVersion = 1;
		if (this.version > 1) {
			dbVersion = this.version;
		}
		String sqlFile = "insert_v" + dbVersion + ".sql";
		Log.i(TAG, "onCreate - insert tables: " + sqlFile);
		loadSqlFile(db, sqlFile);
	}

	@Override
	public final void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.d(TAG, "onUpgrade - START");
/*		if (debugMode) { // Fejlesztői módban minden frissítéskor reseteli a
							// táblát.
			db.execSQL(generateDropQuery(this.databaseTable));
			db.execSQL(generateCreateQuery(this.databaseTable));
		}*/

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

		Log.i(TAG, "onUpgrade - DataBaseHelper.onUpgrade - v" + db.getVersion()
				+ "(" + oldVersion + "," + newVersion + ")");

		for (int i = oldVersion + 1; i < newVersion + 1; i++) {
			String sqlFile = "insert_v" + newVersion + ".sql";
			Log.i(TAG, "onUpgrade - insert/update tables: " + sqlFile);
			loadSqlFile(db, sqlFile);
		}
	}

	private String generateCreateQuery(DatabaseTable databaseTable) {
		Log.d(TAG, "generateCreateQuery - START");
		int columnCount = 0;
		int maxCount = databaseTable.getAllColumn().size();

		StringBuilder sb = new StringBuilder("CREATE TABLE "
				+ databaseTable.getName());
		sb = sb.append("(").append(
				databaseTable.getPrimaryColumn().getColumnName());
		sb = sb.append(" ").append(
				databaseTable.getPrimaryColumn().getTypeString());
		sb = sb.append(" PRIMARY KEY");

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

	private String generateDropQuery(DatabaseTable databaseTable) {
		Log.d(TAG, "generateDropQuery - START");
		StringBuilder sb = new StringBuilder("DROP TABLE IF EXISTS "
				+ databaseTable.getName());
		Log.d(TAG, "generateDropQuery - query - " + sb.toString());
		return sb.toString();
	}
}
