package com.reindeermobile.reindeerutils.mvp;

import com.reindeermobile.reindeerutils.mvp.ViewHandler.IViewTask;
import com.reindeermobile.reindeerutils.mvp.ViewHandler.ViewTask;

public interface IView {
	@Deprecated
	void registerTask(int serviceId, ViewTask viewTask);

	@Deprecated
	void registerTask(String serviceName, ViewTask viewTask);

	void registerViewTask(int serviceId, IViewTask viewTask);

	void registerViewTask(String serviceName, IViewTask viewTask);
}