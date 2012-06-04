package com.reindeermobile.reindeerorm;

import com.reindeermobile.reindeerorm.exception.EntityMappingException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

class EntityManager extends AbstractEntityManager implements
		EntityManagable {
	public static final String TAG = "EntityManager";

	public EntityManager(Context context, String databaseName, int dbVersion) {
		super(context, databaseName, dbVersion);
	}

	@Override
	public <T> T find(T entity, Class<T> clazz) {
		SQLiteDatabase database = baseDbAdaptor.getDatabase();
		DatabaseTable databaseTable = EntityManagerFactory.INSTANCE
				.getDatabaseTable(clazz);
		DatabaseEntity<T> databaseEntity = new DatabaseEntity<T>(entity, databaseTable);

		long id = 0;
		try {
			id = (Long) databaseEntity.getValue(BaseDbAdaptor.COLUMN_ID);
			Cursor cursor = database.query(databaseTable.getName(), null,
					BaseDbAdaptor.COLUMN_ID + " = ?",
					new String[] { String.valueOf(id) }, null, null, null, "1");
			CursorParser<T> cursorParser = new CursorParser<T>(clazz);
			entity = cursorParser.parseCursor(entity, cursor);
		} catch (EntityMappingException exception) {
			Log.w(TAG, "find - ", exception);
		}

		return entity;
	}

	@Override
	public <T> T find(long id, Class<T> clazz) {
		SQLiteDatabase database = baseDbAdaptor.getDatabase();
		DatabaseTable databaseTable = EntityManagerFactory.INSTANCE
				.getDatabaseTable(clazz);
		T entity = null;

		Cursor cursor = database.query(databaseTable.getName(), null,
				BaseDbAdaptor.COLUMN_ID + " = ?",
				new String[] { String.valueOf(id) }, null, null, null, "1");

		CursorParser<T> cursorParser = new CursorParser<T>(clazz);
		entity = cursorParser.parseCursor(entity, cursor);
		return entity;
	}

	public <T> void persist(T entity, Class<T> clazz)
			throws EntityMappingException {
		SQLiteDatabase database = baseDbAdaptor.getDatabase();
		DatabaseTable databaseTable = EntityManagerFactory.INSTANCE
				.getDatabaseTable(clazz);
		DatabaseEntity<T> databaseEntity = new DatabaseEntity<T>(entity, databaseTable);

		long id = database.insert(databaseTable.getName(), null,
				databaseEntity.contentValues());

		databaseEntity.setValue(BaseDbAdaptor.COLUMN_ID, id);
		entity = databaseEntity.getEntity();
	}

	public <T> T merge(T entity, Class<T> clazz)
			throws EntityMappingException {
		SQLiteDatabase database = baseDbAdaptor.getDatabase();
		DatabaseTable databaseTable = EntityManagerFactory.INSTANCE
				.getDatabaseTable(clazz);
		DatabaseEntity<T> databaseEntity = new DatabaseEntity<T>(entity, databaseTable);

		database.update(databaseTable.getName(),
				databaseEntity.contentValues(), BaseDbAdaptor.COLUMN_ID
						+ " = ?", new String[] { String.valueOf(databaseEntity
						.getValue(BaseDbAdaptor.COLUMN_ID)) });

		return databaseEntity.getEntity();
	}

	public <T> void remove(T entity, Class<T> clazz) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <T> Query<T> createNamedNativeQuery(String queryName, Class<T> clazz) {
		SQLiteDatabase database = baseDbAdaptor.getDatabase();
		DatabaseTable databaseTable = EntityManagerFactory.INSTANCE
				.getDatabaseTable(clazz);
		return new NativeQuery<T>(databaseTable.getNativeQuery(queryName),
				database, clazz);
	}

}
