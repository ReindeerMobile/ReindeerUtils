package com.reindeermobile.reindeerutils.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ViewUtils {
	private static final String JPG_EXTENSION = ".jpg";
	public static final String PNG_EXTENSION = ".png";

	protected ViewUtils() {
		throw new UnsupportedOperationException("This is a Utility class");
	}

	public static InputStream getInputStreamFromAssets(Context context, String path) throws IOException {
		InputStream inputStream = null;
		inputStream = context.getAssets().open(path);
		return inputStream;
	}

	private static InputStream loadImageStreamFromAssets(Context context, String imageFile) throws IOException {
		InputStream inputStream = null;
		inputStream = context.getAssets().open(imageFile + JPG_EXTENSION);
		return inputStream;
	}

	private static InputStream loadImageStream(String path) throws FileNotFoundException {
		InputStream inputStream = null;
		inputStream = new FileInputStream(path);
		return inputStream;
	}

	public static int dipToPixel(Context context, float dp) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int pixels = (int) (metrics.density * dp + 0.5f);
		return pixels;
	}

	public static Bitmap getBitmapFromInputStream(InputStream inputStream) {
		if (inputStream != null) {
			return BitmapFactory.decodeStream(inputStream);
		} else {
			return null;
		}
	}

}
