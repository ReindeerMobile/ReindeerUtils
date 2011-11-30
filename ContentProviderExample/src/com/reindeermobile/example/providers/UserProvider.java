package com.reindeermobile.example.providers;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.reindeermobile.example.providers.User.Users;

public class UserProvider extends ContentProvider {
	private static final String LOG_TAG = "userprovider";

	/* ez csak az adatbázis kezeléshez kell */
	private static final String DATABASE_NAME = "userdb";
	private static final int DATABASE_VERSION = 1;

	public static final String PROVIDER_NAME = "com.reindeermobile.example.providers.userprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://"+ PROVIDER_NAME + "/" + Users.USERS_TABLE_NAME);
	
	private static final UriMatcher sUriMatcher;
	private static HashMap<String, String> usersProjectionMap;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d("database", "UserProvider.onCreate: START");
			db.execSQL("CREATE TABLE " + Users.USERS_TABLE_NAME + " (" + Users.USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + Users.EMAIL + " VARCHAR(255),"
					+ Users.NAME + "  VARCHAR(255)" + ");");
			Log.d("database", "UserProvider.onCreate: END");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d("database", "UserProvider.onUpgrade: START");
			Log.w("database", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + Users.USERS_TABLE_NAME);
			onCreate(db);
			Log.d("database", "UserProvider.onUpgrade: END");
		}
	}

	private DatabaseHelper dbHelper;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		Log.d("database", "UserProvider.getType: START");
        switch (sUriMatcher.match(uri)) {
            case Users.USERS:
                return Users.CONTENT_TYPE; //ez még nem biztos, hogy jóra hivatkozik
            case Users.USERS_ID:
                return Users.CONTENT_ITEM_TYPE; //ez még nem biztos, hogy jóra hivatkozik

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues argValues) {
		Log.d("database", "UserProvider.insert: START");
        if (sUriMatcher.match(uri) != Users.USERS) { throw new IllegalArgumentException("Unknown URI " + uri); }

        ContentValues values;
        if (argValues != null) {
            values = new ContentValues(argValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(Users.USERS_TABLE_NAME, Users.EMAIL, values);
        if (rowId > 0) {
            Uri userUri = ContentUris.withAppendedId(Users.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(userUri, null);
            Log.d("database", "UserProvider.insert: END");
            return userUri;
        }
        
        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
        return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.d("database", "UserProvider.query: START");
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case Users.USERS:
                qb.setTables(Users.USERS_TABLE_NAME);
                qb.setProjectionMap(usersProjectionMap);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        Log.d("database", "UserProvider.query: END");
        return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(PROVIDER_NAME, Users.USERS_TABLE_NAME, Users.USERS);
		sUriMatcher.addURI(PROVIDER_NAME, Users.USERS_TABLE_NAME + "/#",Users.USERS_ID);

		usersProjectionMap = new HashMap<String, String>();
		usersProjectionMap.put(Users.USER_ID, Users.USER_ID);
		usersProjectionMap.put(Users.EMAIL, Users.EMAIL);
		usersProjectionMap.put(Users.NAME, Users.NAME);
	}

}
