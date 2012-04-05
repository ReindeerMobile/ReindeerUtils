package com.reindeermobile.reindeerutils.mvp;

import com.reindeermobile.reindeerutils.mvp.AbstractView.ViewTask;

public interface IView {
	void registerTask(int serviceId,
			ViewTask viewTask);
}