package com.reindeermobile.reindeerutils.mvp;

import com.reindeermobile.reindeerutils.mvp.exceptions.ServiceNotRegisteredException;

import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

public class ViewHandler implements Callback, IView {
	public static final String TAG = "ViewHandler";

	@Deprecated
	public abstract class ViewTask {
		public abstract void execute(MessageObject messageObject);
	}

	public interface IViewTask {
		void execute(Object obj, Bundle bundle);
	}

	private final SparseArray<ViewHandler.IViewTask> iViewSparseArray = new SparseArray<ViewHandler.IViewTask>();

	protected String name;

	public ViewHandler(String name) {
		super();
		this.name = name;
	}

	@Override
	public final boolean handleMessage(Message msg) {
		Log.d(TAG, "handleMessage - msg" + msg);
		IViewTask viewTask = this.iViewSparseArray.get(msg.what);
		Log.d(TAG, "handleMessage - task: " + viewTask);
		if (viewTask != null) {
			viewTask.execute(msg.obj, msg.getData());
		}
		return false;
	}

	@Override
	public final void registerViewTask(int serviceId, IViewTask viewTask) {
		Log.i(TAG, "registerTask - register: " + serviceId);
		iViewSparseArray.put(serviceId, viewTask);
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
