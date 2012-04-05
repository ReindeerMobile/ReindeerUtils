package com.reindeermobile.reindeerutils.mvp;

import android.app.Activity;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractView extends Activity implements Callback, IView {
	public static final String TAG = "AbstractView";

	public abstract class ViewTask {
		public abstract void execute(MessageObject messageObject);
	}

	private static final Map<Integer, ViewTask> VIEW_TASK_MAP = new HashMap<Integer, AbstractView.ViewTask>();

	@Override
	public final boolean handleMessage(Message msg) {
		MessageObject messageObject = null;
		if (msg.obj != null && msg.obj instanceof MessageObject) {
			messageObject = (MessageObject) msg.obj;
		}

		ViewTask task = VIEW_TASK_MAP.get(msg.what);
		if (task != null) {
			task.execute(messageObject);
		}

		return false;
	}

	public final void registerTask(int serviceId, ViewTask viewTask) {
		Log.i(TAG, "registerTask - register: " + serviceId);
		VIEW_TASK_MAP.put(serviceId, viewTask);
	}

	public final void registerTask(String serviceName, ViewTask viewTask) {
		Log.i(TAG, "registerTask - register: " + serviceName);
		this.registerTask(Presenter.getInst().getModelServiceId(serviceName),
				viewTask);
	}
}
