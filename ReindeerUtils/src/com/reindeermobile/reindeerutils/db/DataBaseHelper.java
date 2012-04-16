package com.reindeermobile.reindeerutils.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.reindeermobile.reindeerutils.view.StringUtils;
import com.reindeermobile.reindeerutils.view.ViewUtils;

public class DataBaseHelper extends AbstractDatabaseHelper {
	public static final String TAG = "DataBaseHelper";
	
	private String packagePath;
	private String databaseFileName;
	private Context context;

	protected int version;

	public DataBaseHelper(final Context context, final String databaseFileName,
			final int version) {
		super(context, databaseFileName, version);
		this.version = version;

		Log.i(TAG, "DataBaseHelper - version: " + version);
		this.setContext(context);
		this.setPackagePath(context.getPackageName());

		String databaseFullPath = AbstractDatabaseHelper.DATA_SLASH_DATA_PATH + getPackagePath()
				+ StringUtils.PER_STRING + databaseFileName;
		Log.i(TAG, "DataBaseHelper - Database path: " + databaseFullPath);
		this.setDatabaseFileName(databaseFullPath);
	}

	/**
	 * A create_<db_version>.sql és az insert_<db_version>.sql fut le.
	 */
	@Override
	public void onCreate(final SQLiteDatabase db) {
		Log.i(TAG, "onCreate - v1");
		this.init(db);
	}

	/**
	 * A oldVersion+1 -től newVersion-ig fut le. drop_<version_iterator>.sql,
	 * alter_<version_iterator>.sql.
	 * 
	 * @param oldVersion
	 * @param newVersion
	 */
	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
			final int newVersion) {
		Log.i(TAG, "onUpgrade - DataBaseHelper.onUpgrade - v" + db.getVersion()
				+ "(" + oldVersion + "," + newVersion + ")");

		this.update(db, oldVersion, newVersion);
	}
	
	@Override
	protected void init(SQLiteDatabase database) {
		int dbVersion = 1;
		if (this.version > 1) {
			dbVersion = this.version;
		}
		String sqlFile = "create_v" + dbVersion + ".sql";
		Log.i(TAG, "onCreate - create tables: " + sqlFile);
		loadSqlFile(database, sqlFile);

		sqlFile = "insert_v" + dbVersion + ".sql";
		Log.i(TAG, "onCreate - insert datas: " + sqlFile);
		loadSqlFile(database, sqlFile);
	}
	
	@Override
	protected void update(SQLiteDatabase database, int oldVersion, int newVersion) {
		for (int i = oldVersion + 1; i < newVersion + 1; i++) {
			String sqlFile = "drop_v" + newVersion + ".sql";
			Log.i(TAG, "onUpgrade - drop tables: " + sqlFile);
			loadSqlFile(database, sqlFile);

			sqlFile = "alter_v" + newVersion + ".sql";
			Log.i(TAG, "onUpgrade - alter tables: " + sqlFile);
			loadSqlFile(database, sqlFile);
		}
	}

	protected void loadSqlFile(final SQLiteDatabase db, String sqlFileName) {
//		if (!(new File(sqlFileName).exists())) {
//			Log.w(TAG, "loadSqlFile - File not found: " + sqlFileName);
//			return;
//		}

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		String line = StringUtils.EMPTY_STRING;
		try {
			inputStream = ViewUtils.getInputStreamFromAssets(this.getContext(),
					sqlFileName);
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream));

			while ((line = bufferedReader.readLine()) != null) {
				if (line.length() > 0) {
					Log.d(TAG, "loadSqlFile - loadSqlFile: loaded line - "
							+ line);
					db.execSQL(line);
				}
			}
		} catch (IOException e) {
			Log.w(TAG, "loadSqlFile - IO error during", e);
		} catch (SQLException e) {
			Log.w(TAG, e.getMessage() + "(" + line + ")", e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					Log.w(TAG, "loadSqlFile - Can't close buffered reader: :",
							e);
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Log.w(TAG, "loadSqlFile - Can't close buffered reader: :",
							e);
				}
			}
		}
	}

	public final String getDatabaseFileName() {
		return databaseFileName;
	}

	public final void setDatabaseFileName(final String databaseFileName) {
		this.databaseFileName = databaseFileName;
	}

	public final String getPackagePath() {
		return packagePath;
	}

	public final void setPackagePath(final String packagePath) {
		this.packagePath = packagePath;
	}

	public final Context getContext() {
		return context;
	}

	public final void setContext(final Context context) {
		this.context = context;
	}

}
