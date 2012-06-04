package com.reindeermobile.reindeerorm;


import android.content.Context;

public abstract class AbstractEntityManager {
	public static final String TAG = "AbstractEntityManager";

	protected BaseDbAdaptor baseDbAdaptor;

	AbstractEntityManager(Context context, String databaseName, int dbVersion) {
		super();
		this.baseDbAdaptor = new BaseDbAdaptor(context, databaseName, dbVersion);
	}

}
