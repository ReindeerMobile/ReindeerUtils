package com.reindeermobile.reindeerutils.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ViewUtils {
	public static final String JPG_EXTENSION = ".jpg";
	public static final String PNG_EXTENSION = ".png";

	private static final int DEFAULT_BUFFER = 1024;

	protected ViewUtils() {
		throw new UnsupportedOperationException("This is a Utility class");
	}

	public static InputStream getInputStreamFromAssets(Context context, String path) throws IOException {
		InputStream inputStream = null;
		inputStream = context.getAssets().open(path);
		return inputStream;
	}

	public static InputStream loadJpgStreamFromAssets(Context context, String imageFile) throws IOException {
		InputStream inputStream = null;
		inputStream = context.getAssets().open(imageFile + JPG_EXTENSION);
		return inputStream;
	}
	
	public static InputStream loadPngStreamFromAssets(Context context, String imageFile) throws IOException {
		InputStream inputStream = null;
		inputStream = context.getAssets().open(imageFile + PNG_EXTENSION);
		return inputStream;
	}

	public static InputStream loadImageStream(String path) throws FileNotFoundException {
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
	
	public static void streamCopy(final InputStream inputStream, final OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER];
		int length;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		inputStream.close();
		outputStream.close();
	}

}
