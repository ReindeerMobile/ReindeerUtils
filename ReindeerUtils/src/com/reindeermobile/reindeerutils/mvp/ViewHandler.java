package com.reindeermobile.reindeerutils.mvp;

import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class ViewHandler implements Callback, IView {
	public static final String TAG = "ViewHandler";

	public abstract class ViewTask {
		public abstract void execute(MessageObject messageObject);
	}

	private static final Map<Integer, ViewTask> VIEW_TASK_MAP = new HashMap<Integer, ViewTask>();

	protected String name;

	public ViewHandler(String name) {
		super();
		this.name = name;
	}

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
		try {
			this.registerTask(Presenter.getInst()
					.getModelServiceId(serviceName), viewTask);
		} catch (ServiceNotRegisteredException exception) {
			Log.w(TAG, "registerTask - service not found:", exception);
		}
	}

	@Override
	public String toString() {
		return "ViewHandler [name=" + this.name + "]";
	}
}
