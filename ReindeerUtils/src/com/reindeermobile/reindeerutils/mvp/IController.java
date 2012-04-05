package com.reindeermobile.reindeerutils.mvp;

import android.content.Context;
import android.os.Handler.Callback;

public interface IController extends Callback {
	void init(Context context);
}
