package com.reindeermobile.reindeerutils.mvp;

import com.reindeermobile.reindeerutils.mvp.AbstractController.ContollerTask;

import android.content.Context;
import android.os.Handler.Callback;

public interface IController extends Callback {
	void init(Context context);

	void registerTask(int serviceId, ContollerTask contollerTask);

	void registerTask(String serviceName, ContollerTask contollerTask);
}
