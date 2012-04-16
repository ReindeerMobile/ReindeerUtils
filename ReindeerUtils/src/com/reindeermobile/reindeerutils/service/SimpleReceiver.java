package com.reindeermobile.reindeerutils.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class SimpleReceiver extends ResultReceiver {
	private Receiver mReceiver;

	public SimpleReceiver(Handler handler) {
		super(handler);
	}

	public void setReceiver(Receiver receiver) {
		mReceiver = receiver;
	}

	public interface Receiver {
		public void onReceiveResult(int resultCode, Bundle resultData);
	}

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		if (mReceiver != null) {
			mReceiver.onReceiveResult(resultCode, resultData);
		}
	}

}
