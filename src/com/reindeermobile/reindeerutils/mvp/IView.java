package com.reindeermobile.reindeerutils.mvp;

import com.reindeermobile.reindeerutils.mvp.ViewHandler.ViewTask;

public interface IView {
	void registerTask(int serviceId, ViewTask viewTask);

	void registerTask(String serviceName, ViewTask viewTask);
}