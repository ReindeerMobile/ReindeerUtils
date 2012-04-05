package com.reindeermobile.reindeerutils.mvp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Presenter implements Callback {
	public static final String TAG = "Presenter";

	private static volatile Presenter INSTANCE;

	/**
	 * A modellek listája.
	 */
	private List<Handler> modelHandlerList;
	/**
	 * A nézetek listája.
	 */
	private List<Handler> viewHandlerList;
	private Map<String, Handler> modelHandlerMap;
	private HashMap<Callback, Handler> handlerMap;
	/**
	 * A model szolgáltatás azonosítókhoz rendelt modellek.
	 */
	private Map<Integer, Handler> modelServiceMap;

	// TODO resolv back & forth mapping
	private Map<String, Integer> serviceNameModelIdMap;
	private Context context;
	private Handler handler;
	private HandlerThread handlerThread;

	public static final void initInstance(Context context,
			List<IController> modelList) {
		INSTANCE = new Presenter(context, modelList);

		Log.i(TAG, "initInstance - init models...");
		for (IController controller : modelList) {
			controller.init(context);
		}
		Log.i(TAG, "initInstance - OK");
	}

	public static final Presenter getInst() {
		return INSTANCE;
	}

	
	/**
	 * @deprecated Use instead {@link ControllerServices}!
	 */
	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ModelService {
	}

	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ControllerServices {
	}

	public final int getModelServiceId(String serviceName) {
		// Log.d(TAG, "getModelServiceId - serviceName: " + serviceName);
		// Log.d(TAG, "getModelServiceId - serviceNameModelIdMap.size: "
		// + serviceNameModelIdMap.size());
		return this.serviceNameModelIdMap.get(serviceName);
	}

	public final void subscribe(Callback viewComponentCallback) {
		Handler handler = new Handler(viewComponentCallback);
		this.handlerMap.put(viewComponentCallback, handler);
		this.viewHandlerList.add(handler);
	}

	public final void unsubscribe(Callback viewComponentallback) {
		List<Handler> newList = new ArrayList<Handler>(this.viewHandlerList);
		newList.remove(this.handlerMap.get(viewComponentallback));
		this.handlerMap.remove(viewComponentallback);
		this.viewHandlerList = newList;
	}

	public final void dispose() {
		for (Handler handler : this.modelHandlerList) {
			handler.getLooper().quit();
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}

	public Context getApplicationContext() {
		return this.context;
	}

	public void sendViewMessage(String serviceName) {
		this.sendViewMessage(this.getModelServiceId(serviceName), 0, 0, null);
	}

	public void sendViewMessage(String serviceName, int arg1) {
		this.sendViewMessage(this.getModelServiceId(serviceName), arg1, 0, null);
	}

	public void sendViewMessage(String serviceName, int arg1, MessageObject obj) {
		this.sendViewMessage(this.getModelServiceId(serviceName), arg1, 0, obj);
	}

	public void sendViewMessage(String serviceName, MessageObject obj) {
		this.sendViewMessage(this.getModelServiceId(serviceName), 0, 0, obj);
	}

	public void sendViewMessage(int what) {
		this.sendViewMessage(what, 0, 0, null);
	}

	public void sendViewMessage(int what, int arg1) {
		this.sendViewMessage(what, arg1, 0, null);
	}

	public void sendViewMessage(int what, int arg1, MessageObject obj) {
		this.sendViewMessage(what, arg1, 0, obj);
	}

	public void sendViewMessage(int what, MessageObject obj) {
		this.sendViewMessage(what, 0, 0, obj);
	}

	/**
	 * View üzenetet küld a {@link MessageObject#getModelName()}-től a
	 * {@link MessageObject#getSenderView()}-nak.
	 * 
	 * @param what
	 * @param obj
	 */
	public final void sendViewMessage(int what, int arg1, int arg2,
			MessageObject obj) {
		if (obj != null && obj.getSenderView() != null) {
			Log.d(TAG, "sendViewMessage - what: " + what);
			sendMessageToTarget(this.handlerMap.get(obj.getSenderView()), what,
					arg1, arg2, obj, null);
		} else {
			Log.d(TAG, "sendViewMessage - what: " + what);
			List<Handler> outBoxList = new ArrayList<Handler>(
					this.viewHandlerList);
			for (Handler handler : outBoxList) {
				sendMessageToTarget(handler, what, arg1, arg2, obj, null);
			}
		}
	}

	public void sendModelMessage(String serviceName) {
		this.sendModelMessage(this.getModelServiceId(serviceName), 0, 0, null);
	}

	public void sendModelMessage(String serviceName, int arg, MessageObject obj) {
		this.sendModelMessage(this.getModelServiceId(serviceName), arg, 0, obj);
	}

	public void sendModelMessage(String serviceName, MessageObject obj) {
		this.sendModelMessage(this.getModelServiceId(serviceName), 0, 0, obj);
	}

	public void sendModelMessage(int what) {
		this.sendModelMessage(what, 0, 0, null);
	}

	public void sendModelMessage(int what, int arg, MessageObject obj) {
		this.sendModelMessage(what, arg, 0, obj);
	}

	public void sendModelMessage(int what, MessageObject obj) {
		this.sendModelMessage(what, 0, 0, obj);
	}

	public final void sendModelMessage(int what, int arg1, int arg2,
			MessageObject obj) {
		Log.d(TAG, "sendModelMessage - START: " + obj);
		Handler targetHandler = this.modelServiceMap.get(what);
		Log.d(TAG, "sendModelMessage - what: " + what);
		this.sendMessageToTarget(targetHandler, what, arg1, arg2, obj, null);
		Message.obtain(handler, what, arg1, arg2, obj).sendToTarget();
		Log.d(TAG, "sendModelMessage - END");
	}

	private Presenter(Context context, List<IController> modelList) {
		this.context = context;
		Log.d(TAG, "Presenter - START");
		this.handlerMap = new HashMap<Callback, Handler>();
		this.modelHandlerMap = new HashMap<String, Handler>();
		this.modelServiceMap = new HashMap<Integer, Handler>();
		this.serviceNameModelIdMap = new HashMap<String, Integer>();

		this.modelHandlerList = new ArrayList<Handler>(modelList.size());
		this.viewHandlerList = new ArrayList<Handler>();

		for (int i = 0; i < modelList.size(); i++) {
			String modelClassName = modelList.get(i).getClass().getName();

			// Init model handler.
			HandlerThread handlerThread = new HandlerThread(modelClassName);
			handlerThread.start();
			Handler modelHandler = new Handler(handlerThread.getLooper(),
					modelList.get(i));

			// Associate model services to model handler.
			this.fetchModelServices(modelHandler, modelList.get(i));
			this.fetchControllerServices(modelHandler, modelList.get(i));

			this.modelHandlerList.add(modelHandler);
			this.modelHandlerMap.put(modelClassName, modelHandler);
		}

		// TODO ezt a rész még nem vágom
		this.handlerThread = new HandlerThread(getClass().getCanonicalName());
		this.handlerThread.start();
		this.handler = new Handler(this.handlerThread.getLooper(), this);
	}

	@Deprecated
	private void fetchModelServices(Handler modelHandler, IController controller) {
		Field[] fields = controller.getClass().getFields();
		for (int fieldIndex = 0; fieldIndex < fields.length; fieldIndex++) {
			if (fields[fieldIndex].isAnnotationPresent(ModelService.class)) {
				try {
					int serviceId = fields[fieldIndex].getInt(controller);
					// String serviceName = fields[fieldIndex].getName();
					this.modelServiceMap.put(serviceId, modelHandler);
				} catch (IllegalArgumentException exception) {
					Log.w(TAG, "fetchModelServices:", exception);
				} catch (IllegalAccessException exception) {
					Log.w(TAG, "fetchModelServices:", exception);
				}
			}
		}
	}

	private void fetchControllerServices(Handler modelHandler,
			IController controller) {
		Log.i(TAG, "fetchControllerServices - START");
		Field[] fields = controller.getClass().getFields();
		for (int fieldIndex = 0; fieldIndex < fields.length; fieldIndex++) {
			if (fields[fieldIndex]
					.isAnnotationPresent(ControllerServices.class)) {
				try {
					String[] serviceNames = (String[]) fields[fieldIndex]
							.get(null);
					for (String serviceName : serviceNames) {
						int serviceId = (int) System.currentTimeMillis();
						this.modelServiceMap.put(serviceId, modelHandler);
						this.serviceNameModelIdMap.put(serviceName, serviceId);
						Log.i(TAG, "fetchControllerServices - serviceName: "
								+ serviceName);
					}
				} catch (IllegalArgumentException exception) {
					Log.w(TAG, "fetchModelServices:", exception);
				} catch (IllegalAccessException exception) {
					Log.w(TAG, "fetchModelServices:", exception);
				}
			}
		}
	}

	private void sendMessageToTarget(Handler handler, int what, int arg1,
			int arg2, Object obj, Bundle bundle) {
		if (handler != null) {
			Message message = Message.obtain(handler, what, arg1, arg2, obj);
			message.setData(bundle);
			message.sendToTarget();
		} else {
			Log.w(TAG, "sendMessageToTarget - handler not found: " + what);
		}
	}

}
