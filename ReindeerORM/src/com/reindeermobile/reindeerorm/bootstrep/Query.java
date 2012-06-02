package com.reindeermobile.reindeerorm.bootstrep;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

public class Query {
	private SQLiteDatabase database;
	private Map<String, String> parameters;
	private Cursor resultCursor;
}
