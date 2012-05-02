package com.reindeermobile.reindeerutils.mvp;

import com.reindeermobile.reindeerutils.mvp.exceptions.ServiceNotRegisteredException;

import android.content.Context;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractController implements IController {
	public static final String TAG = "AbstractController";

	// TODO ContollerTask: Ez inkább legyen interface!
	public abstract class ContollerTask {
		public abstract void execute(Callback sender,
				MessageObject messageObject);
	}

	private static final Map<Integer, ContollerTask> CONTROLLER_TASK_MAP = new HashMap<Integer, AbstractController.ContollerTask>();

//	private Map<String, List<Callback>> viewResponseMap = new HashMap<String, List<Callback>>();

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

	protected abstract void initTasks();

	public void registerTask(int serviceId, ContollerTask contollerTask) {
		Log.i(TAG, "registerTask - register: " + serviceId);
		CONTROLLER_TASK_MAP.put(serviceId, contollerTask);
	}

	public final void registerTask(String serviceName,
			ContollerTask contollerTask) {
		Log.i(TAG, "registerTask - register: " + serviceName);
		try {
			this.registerTask(Presenter.getInst()
					.getControllerServiceId(serviceName), contollerTask);
		} catch (ServiceNotRegisteredException exception) {
			Log.w(TAG, "registerTask - service not registered:", exception);
		}
	}
	
	@Override
	public String toString() {
		return TAG;
	}

//	/**
//	 * A responseServiceId-ra feliratkoztatja a subscriberView-t.
//	 * 
//	 * @param subscriberView
//	 * @param responseServiceId
//	 */
//	protected void subscribeViewToResponse(Callback subscriberView,
//			String responseServiceId) {
//		Log.d(TAG, "subscribeViewToResponse - subsrcibe to "
//				+ responseServiceId);
//		if (this.viewResponseMap.get(responseServiceId) != null) {
//			this.viewResponseMap.get(responseServiceId).add(subscriberView);
//		} else {
//			List<Callback> subscribers = Arrays.asList(subscriberView);
//			this.viewResponseMap.put(responseServiceId, subscribers);
//		}
//	}
//
//	/**
//	 * A responseName-re feliratkozott nézeteknek elküldi az object-et.
//	 * 
//	 * @param responseName
//	 * @param object
//	 */
//	protected void sendResponseToSubscribers(String responseName, Object object) {
//		if (this.viewResponseMap.containsKey(responseName)) {
//			for (Callback callback : this.viewResponseMap.get(responseName)) {
//				Presenter.getInst().sendViewMessage(responseName,
//						new MessageObject(callback, object));
//			}
//		}
//	}

}
