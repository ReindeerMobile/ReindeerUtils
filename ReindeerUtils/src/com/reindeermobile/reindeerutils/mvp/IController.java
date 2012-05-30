package com.reindeermobile.reindeerutils.mvp;

import com.reindeermobile.reindeerutils.mvp.AbstractController.IContollerTask;

import android.content.Context;
import android.os.Handler.Callback;

public interface IController extends Callback {
	void init(Context context);

	void registerTask(int serviceId, IContollerTask contollerTask);

	void registerTask(String serviceName, IContollerTask contollerTask);
}
