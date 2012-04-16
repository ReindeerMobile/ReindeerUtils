package com.reindeermobile.reindeerutils.db;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

/*
 * Ami kell:
 * + az aktuális verzió
 * + a szervernek vissza kell adnia a legújabb verziót
 * + a provider tudja, hogy hanyas mettől meddig kell kikérnie a fájlt.
 * + innentől egy next-next módszerrel le kell kérnie az összes fájlt, ami a két verzió között van.
 */
public class RemoteDbProvider {
	public static final String TAG = "RemoteDbProvider";

	private static final int HTTP_TIMEOUT = 3600;

	private DefaultHttpClient httpClient;

	public RemoteDbProvider() {
		super();
		this.httpClient = new DefaultHttpClient();

		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);

		if (this.httpClient != null) {
			Log.i(TAG, "RemoteDbProvider - init http client: success");
		}
	}

}
