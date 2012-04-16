package com.reindeermobile.reindeerutils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class AbstractDatabaseHelper extends SQLiteOpenHelper {

	protected static final String DATA_SLASH_DATA_PATH = "/data/data/";

	public AbstractDatabaseHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}
	
	protected abstract void init(SQLiteDatabase database);
	
	protected abstract void update(SQLiteDatabase database, int oldVersion, int newVersion);

}
