package com.reindeermobile.reindeerutils.mvp;

import android.content.Context;
import android.os.Handler.Callback;

public interface IModel extends Callback {
	void init(Context context);
}
