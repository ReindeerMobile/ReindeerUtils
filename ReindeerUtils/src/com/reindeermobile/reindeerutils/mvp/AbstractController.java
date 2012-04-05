package com.reindeermobile.reindeerutils.mvp;

import android.content.Context;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractController implements IController {
	public static final String TAG = "AbstractController";

	public abstract class ContollerTask {
		public abstract void execute(Callback sender,
				MessageObject messageObject);
	}

	private static final Map<Integer, ContollerTask> CONTROLLER_TASK_MAP = new HashMap<Integer, AbstractController.ContollerTask>();

	@Override
	public final boolean handleMessage(Message msg) {
		Callback sender = null;
		MessageObject messageObject = null;
		if (msg.obj != null && msg.obj instanceof MessageObject) {
			messageObject = (MessageObject) msg.obj;
			sender = messageObject.getSenderView();
		}

		ContollerTask task = CONTROLLER_TASK_MAP.get(msg.what);
		if (task != null) {
			task.execute(sender, messageObject);
		}

		return false;
	}

	@Override
	public abstract void init(Context context);

	public void registerTask(int serviceId, ContollerTask contollerTask) {
		Log.i(TAG, "registerTask - register: " + serviceId);
		CONTROLLER_TASK_MAP.put(serviceId, contollerTask);
	}

	public final void registerTask(String serviceName,
			ContollerTask contollerTask) {
		Log.i(TAG, "registerTask - register: " + serviceName);
		this.registerTask(Presenter.getInst().getModelServiceId(serviceName),
				contollerTask);
	}

}
