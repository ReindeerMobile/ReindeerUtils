package com.reindeermobile.reindeerutils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BaseDbAdaptor {
	public static final String TAG = "BaseDbAdaptor";

	public static final String COLUMN_ID = "_id";

	private DataBaseHelper dataBaseHelper;

	public BaseDbAdaptor(final Context context, final String databaseName,
			int dbVersion) {
		super();
		Log.d(TAG, "BaseDbAdaptor - Initialize DB adaptor");
		setDataBaseHelper(new DataBaseHelper(context, databaseName, dbVersion));
	}

	public final DataBaseHelper getDataBaseHelper() {
		return dataBaseHelper;
	}

	public final void setDataBaseHelper(final DataBaseHelper dataBaseHelper) {
		this.dataBaseHelper = dataBaseHelper;
	}

	public final SQLiteDatabase getDatabase() {
		return getDataBaseHelper().getWritableDatabase();
	}

}
