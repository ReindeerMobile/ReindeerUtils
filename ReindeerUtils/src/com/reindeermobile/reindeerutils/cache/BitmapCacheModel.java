package com.reindeermobile.reindeerutils.cache;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.reindeermobile.reindeerutils.mvp.IModel;
import com.reindeermobile.reindeerutils.mvp.MessageObject;
import com.reindeermobile.reindeerutils.mvp.Presenter;
import com.reindeermobile.reindeerutils.mvp.Presenter.ModelService;
import com.reindeermobile.reindeerutils.view.ViewUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class BitmapCacheModel implements IModel {
	private static final String APP_DATA_FOLDER = "/data/data/com.reindeermobile.newdbexplist/";

	public static final String TAG = "BitmapCacheModel";

	private Context context;

	@ModelService
	public static final int GET_PRODUCT_IMAGE = 200;
	public static final int SEND_IMAGE = 201;

	@ModelService
	public static final int GET_CATEGORY_IMAGE = 202;
	public static final int SEND_CATEGORY_IMAGE = 203;

	private AsyncHttpClient asyncHttpClient;

	public static final String IMAGE_SERVER_URL = "http://192.168.10.200";
	public static final String URL_PRODUCT_IMAGES = IMAGE_SERVER_URL
			+ "/android/images/products/";
	public static final String URL_CATEGORY_IMAGES = IMAGE_SERVER_URL
			+ "/android/images/categories/";

	@Override
	public void init(Context context) {
		this.context = context;
		this.asyncHttpClient = new AsyncHttpClient();
	}

	@Override
	public boolean handleMessage(Message msg) {
		Callback sender = null;
		MessageObject messageObject = null;
		if (msg.obj != null && msg.obj instanceof MessageObject) {
			messageObject = (MessageObject) msg.obj;
			sender = messageObject.getSenderView();
		}
		switch (msg.what) {
		case GET_PRODUCT_IMAGE:
			// Log.d(TAG, "handleMessage - GET_PRODUCT_IMAGE");
			if (messageObject != null && messageObject.hasData(String.class)) {
				final Callback senderFinal = sender;
				final String imageFileName = (String) messageObject.getData();

				if (!(new File(APP_DATA_FOLDER + imageFileName)).exists()) {
					downloadImage(senderFinal, imageFileName,
							BitmapCacheModel.URL_PRODUCT_IMAGES,
							BitmapCacheModel.SEND_IMAGE);
				} else {
					loadFromStore(senderFinal, imageFileName,
							BitmapCacheModel.SEND_IMAGE);
				}
			}
			break;
		case GET_CATEGORY_IMAGE:
			// Log.d(TAG, "handleMessage - GET_CATEGORY_IMAGE");
			if (messageObject != null && messageObject.hasData(String.class)) {
				final Callback senderFinal = sender;
				final String imageFileName = (String) messageObject.getData();

				Log.d(TAG, "handleMessage - get file: " + imageFileName);

				if (!(new File(APP_DATA_FOLDER + imageFileName)).exists()) {
					downloadImage(senderFinal, imageFileName,
							BitmapCacheModel.URL_CATEGORY_IMAGES,
							BitmapCacheModel.SEND_CATEGORY_IMAGE);
				} else {
					loadFromStore(senderFinal, imageFileName,
							BitmapCacheModel.SEND_IMAGE);
				}
			}
			break;
		default:
			break;
		}
		return true;
	}

	private void loadFromStore(final Callback senderFinal,
			final String imageFileName, final int serviceAnswareId) {
		// Log.d(TAG, "loadFromStore - START");
		Bitmap bitmap = null;
		if (BitmapCacheEnum.INSTANCE.containKey(imageFileName)) {
			bitmap = BitmapCacheEnum.INSTANCE.get(imageFileName);
		} else {
			try {
				// Log.d(TAG, "loadFromStore - load: " + APP_DATA_FOLDER
				// + imageFileName);
				bitmap = ViewUtils.getBitmapFromInputStream(ViewUtils
						.loadImageStream(APP_DATA_FOLDER + imageFileName));
				BitmapCacheEnum.INSTANCE.put(imageFileName, bitmap);
			} catch (FileNotFoundException exception) {
				exception.printStackTrace();
			}
		}
		Presenter.getInst().sendViewMessage(serviceAnswareId,
				new MessageObject(senderFinal, bitmap));
		// Log.d(TAG, "loadFromStore - END");
	}

	private void downloadImage(final Callback senderFinal,
			final String imageFileName, String url, final int serviceAnswareId) {
		// Log.d(TAG, "downloadImage - download image from: " + url +
		// imageFileName);
		this.asyncHttpClient.get(url + imageFileName,
				new ImageDownloadRespondHandler(senderFinal, imageFileName,
						serviceAnswareId));
		// Log.d(TAG, "downloadImage - END");
	}

	public class ImageDownloadRespondHandler extends AsyncHttpResponseHandler {
		private String imageFile;
		private int serviceId;
		private Callback senderCallback;

		public ImageDownloadRespondHandler(Callback senderCallback,
				String imageFile, int serviceId) {
			this.senderCallback = senderCallback;
			this.imageFile = imageFile;
			this.serviceId = serviceId;
		}

		@Override
		public void onSuccess(String content) {
			super.onSuccess(content);
			Bitmap bitmap = null;
			try {
				// Log.d(TAG, "onSuccess - not exists, start copy..."
				// + APP_DATA_FOLDER + imageFile);
				ViewUtils
						.streamCopy(
								new ByteArrayInputStream(content
										.getBytes("ISO-8859-1")),
								new FileOutputStream(APP_DATA_FOLDER
										+ imageFile));

				bitmap = ViewUtils.getBitmapFromInputStream(ViewUtils
						.loadImageStream(APP_DATA_FOLDER + imageFile));

				BitmapCacheEnum.INSTANCE.put(imageFile, bitmap);
			} catch (UnsupportedEncodingException exception) {
				exception.printStackTrace();
			} catch (FileNotFoundException exception) {
				exception.printStackTrace();
			} catch (IOException exception) {
				exception.printStackTrace();
			} finally {
				// Log.d(TAG, "onSuccess - send image to caller");
				Presenter.getInst().sendViewMessage(serviceId,
						new MessageObject(senderCallback, bitmap));
			}
		}
	}

}
