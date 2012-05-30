package com.reindeermobile.reindeerutils.mvp;

import com.reindeermobile.reindeerutils.mvp.ViewHandler.IViewTask;

public interface IView {
	void registerViewTask(int serviceId, IViewTask viewTask);

	void registerViewTask(String serviceName, IViewTask viewTask);
}