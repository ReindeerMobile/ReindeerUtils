package com.reindeermobile.reindeerorm.entitymanager;

import com.reindeermobile.reindeerorm.BaseDbAdaptor;
import com.reindeermobile.reindeerorm.DatabaseTable;
import com.reindeermobile.reindeerorm.bootstrep.EntityManagerFactory;
import com.reindeermobile.reindeerorm.exception.EntityMappingException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EntityManager extends AbstractEntityManager implements
		EntityManagable {
	public static final String TAG = "EntityManager";

	/*
	 * Maybe PersistanceContext annotation?
	 */
	public EntityManager(Context context, String databaseName,
			int dbVersion) {
		super(context, databaseName, dbVersion);
	}

	@Override
	public <T> T find(T entity, Class<T> clazz) {
		/*
		 * what: kapunk egy entitást és az adatbázisban található megfelelőjét
		 * kérjük ki. Ezt az entitás elsődleges kulcsa alapján tesszük meg.
		 * 
		 * select * from <tablename> where <id> = entity.id
		 */

		SQLiteDatabase database = baseDbAdaptor.getDatabase();
		DatabaseTable databaseTable = EntityManagerFactory.INSTANCE.getDatabaseTable(clazz);
		long id = 0;
		try {
			id = (Long) databaseTable.getEntityFieldValueByColumnName(
					entity, BaseDbAdaptor.COLUMN_ID);
			
			Cursor cursor = database.query(databaseTable.getName(), null,
					BaseDbAdaptor.COLUMN_ID + " = ?", new String[] { "" },
					null, null, null, "1");
			entity = parseCursor(entity, cursor, clazz, databaseTable);
		} catch (EntityMappingException exception) {
			// TODO Auto-generated catch block
			exception.printStackTrace();
		}

		return entity;
	}

	@Override
	public <T> T find(long id, Class<T> clazz) {
		return null;
	}

}
