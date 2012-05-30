package com.reindeermobile.reindeerorm;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

@Deprecated
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
