package com.reindeermobile.reindeerutils.mvp;

import com.reindeermobile.reindeerutils.mvp.exceptions.ServiceNotRegisteredException;

import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class ViewHandler implements Callback, IView {
	public static final String TAG = "ViewHandler";

	@Deprecated
	public abstract class ViewTask {
		public abstract void execute(MessageObject messageObject);
	}

	public interface IViewTask {
		void execute(Object obj, Bundle bundle);
	}

	@Deprecated
	private final Map<Integer, ViewTask> viewTasksMap = new HashMap<Integer, ViewTask>();
	private final Map<Integer, IViewTask> iViewTasksMap = new HashMap<Integer, ViewHandler.IViewTask>();

	protected String name;

	public ViewHandler(String name) {
		super();
		this.name = name;
	}

	@Override
	public final boolean handleMessage(Message msg) {
		Log.d(TAG, "handleMessage - msg" + msg);
		MessageObject messageObject = null;
		if (msg.obj != null && msg.obj instanceof MessageObject) {
			messageObject = (MessageObject) msg.obj;
		}

		ViewTask task = viewTasksMap.get(msg.what);
		Log.d(TAG, "handleMessage - task: " + task);
		if (task != null) {
			task.execute(messageObject);
		}

		IViewTask viewTask = iViewTasksMap.get(msg.what);
		Log.d(TAG, "handleMessage - task: " + viewTask);
		if (viewTask != null) {
			viewTask.execute(msg.obj, msg.getData());
		}

		return false;
	}

	@Deprecated
	public final void registerTask(int serviceId, ViewTask viewTask) {
		Log.i(TAG, "registerTask - register: " + serviceId);
		viewTasksMap.put(serviceId, viewTask);
	}

	@Deprecated
	public final void registerTask(String serviceName, ViewTask viewTask) {
		Log.i(TAG, "registerTask - register: " + serviceName);
		try {
			this.registerTask(
					Presenter.getInst().getViewServiceId(serviceName), viewTask);
		} catch (ServiceNotRegisteredException exception) {
			Log.w(TAG, "registerTask - service not found:", exception);
		}
	}

	@Override
	public final void registerViewTask(int serviceId, IViewTask viewTask) {
		Log.i(TAG, "registerTask - register: " + serviceId);
		iViewTasksMap.put(serviceId, viewTask);
	}

	@Override
	public final void registerViewTask(String serviceName, IViewTask viewTask) {
		Log.i(TAG, "registerTask - register: " + serviceName);
		try {
			this.registerViewTask(
					Presenter.getInst().getViewServiceId(serviceName), viewTask);
		} catch (ServiceNotRegisteredException exception) {
			Log.w(TAG, "registerTask - service not found:", exception);
		}
	}

	@Override
	public String toString() {
		return "ViewHandler [name=" + this.name + "]";
	}
}
