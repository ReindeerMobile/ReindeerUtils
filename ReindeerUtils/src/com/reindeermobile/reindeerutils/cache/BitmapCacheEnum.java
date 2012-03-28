package com.reindeermobile.reindeerutils.cache;

import com.google.common.collect.MapMaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public enum BitmapCacheEnum {
	INSTANCE;

	public static final String TAG = "BitmapCacheEnum";

	private Object context;
	private ConcurrentMap<String, Bitmap> bitmapCache;
	private int MAX_CAPACITY = 40;

	public void init(Context context) {
		if (this.context == null) {
			this.context = context;
			MapMaker mapMaker = new MapMaker();
			mapMaker.initialCapacity(20);
			mapMaker.expiration(5 * 60, TimeUnit.SECONDS);
			mapMaker.concurrencyLevel(16);
//			mapMaker.softValues();
			this.bitmapCache = mapMaker.makeMap();
		}
	}

	public void put(String key, Bitmap bitmap) {
		if (this.bitmapCache.size() >= MAX_CAPACITY) {
			this.bitmapCache.clear();
		}
		this.bitmapCache.put(key, bitmap);
//		Log.d(TAG, "put - mapped: " + key);
	}

	public Bitmap get(String key) {
		if (key != null && this.bitmapCache != null) {
//			Log.d(TAG, "get - get bitmap by " + key);
			return this.bitmapCache.get(key);
		} else {
			return null;
		}
	}

	public boolean containKey(String key) {
		return (key != null && this.bitmapCache != null && this.bitmapCache
				.containsKey(key));
	}
}
