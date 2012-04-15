package com.reindeermobile.reindeerutils.db;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

import java.io.IOException;
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
	private static final String SQL_PREFIX = "insert_";
	
	private String url;
	private DefaultHttpClient httpClient;
	
	private int dbVersion;

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
	
    private HttpResponse getSqlByVersion(int version) throws ClientProtocolException, IOException {
    	Log.d(TAG, "getSql - START");
    	String url = this.url + SQL_PREFIX + version + ".sql";
    	Log.d(TAG, "getSql - url: " + url);
        HttpPost httpPost = new HttpPost(url);
        HttpResponse response = null;
        try {
            response = this.httpClient.execute(httpPost);
        } catch (IOException exception) {
            Log.w(TAG, "send - " + exception.getMessage(), exception);
        }
        Log.d(TAG, "getSql - END");
        return response;
    }

}
