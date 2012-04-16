package com.reindeermobile.reindeerutils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RemoteMappedDatabaseHelper extends MappedDataBaseHelper {
	public static final String TAG = "RemoteMappedDatabaseHelper";

	private InputStream inputStream;

	public RemoteMappedDatabaseHelper(Context context,
			InputStream sqlInputStream, String name, int version,
			DatabaseTable databaseTable) {
		super(context, name, version, databaseTable);
		this.inputStream = sqlInputStream;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.d(TAG, "onCreate - START");
		super.onCreate(database);
		this.init(database);
	}

	@Override
	protected void init(SQLiteDatabase database) {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(
					this.inputStream));
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				database.rawQuery(line, null);
			}
		} catch (IOException exception) {
			Log.w(TAG, "init - exception", exception);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException exception) {
					Log.w(TAG, "init - exception + ", exception);
				}
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onUpgrade(db, oldVersion, newVersion);
	}

}
