package com.reindeermobile.reindeerutils.mvp;

import com.reindeermobile.reindeerutils.mvp.exceptions.ServiceNotRegisteredException;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

public abstract class AbstractController implements IController {
	public static final String TAG = "AbstractController";

	public interface IContollerTask {
		void execute(Bundle bundle);
	}

	private static final SparseArray<AbstractController.IContollerTask> ICONTROLLER_TASK_SPARSE_ARRAY = new SparseArray<AbstractController.IContollerTask>();

	@Override
	public final boolean handleMessage(Message msg) {
		IContollerTask task = ICONTROLLER_TASK_SPARSE_ARRAY.get(msg.what);
		if (task != null) {
			task.execute(msg.getData());
		}
		return false;
	}

	@Override
	public abstract void init(Context context);

	protected abstract void initTasks();

	public void registerTask(int serviceId, IContollerTask contollerTask) {
		Log.i(TAG, "registerTask - register: " + serviceId);
		ICONTROLLER_TASK_SPARSE_ARRAY.put(serviceId, contollerTask);
	}

	public final void registerTask(String serviceName,
			IContollerTask contollerTask) {
		Log.i(TAG, "registerTask - register: " + serviceName);
		try {
			this.registerTask(
					Presenter.getInst().getControllerServiceId(serviceName),
					contollerTask);
		} catch (ServiceNotRegisteredException exception) {
			Log.w(TAG, "registerTask - service not registered:", exception);
		}
	}

	@Override
	public String toString() {
		return TAG;
	}

}
