package com.reindeermobile.reindeerutils.mvp;

import com.reindeermobile.reindeerutils.mvp.exceptions.HandlerNotFoundException;
import com.reindeermobile.reindeerutils.mvp.exceptions.ServiceNotFoundException;
import com.reindeermobile.reindeerutils.mvp.exceptions.ServiceNotRegisteredException;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Ez az osztály játsza a "forgalomirányító" szerepét az MVP modelben.
 * 
 * @author zsdoma@reindeermobile.com
 * 
 */
public final class Presenter implements Callback {
	public static final String TAG = "Presenter";

	private static volatile Presenter INSTANCE;

	private int serviceIdCounter = 1;

	/**
	 * A nézeteket meppeli fel a Callback példány alapján.
	 */
	private Map<Callback, Handler> viewHandlerMap;

	/**
	 * A Controller Handler-eket meppeli fel a ControllerService név alapján.
	 */
	private Map<String, Handler> controllerHandlerMap;

	/**
	 * A ViewService-ekhez rendeli hozzá a nézetek Callback példányait.
	 */
	private Map<String, List<Callback>> viewCallbacksMap;

	// TODO resolv back & forth mapping
	private Map<String, Integer> controllerServiceIdMap;
	private Map<Integer, String> controllerServiceNameMap;

	private Map<String, Integer> viewServiceIdMap;
	private Map<Integer, String> viewServiceNameMap;

	/*
	 * A model szolgáltatás azonosítókhoz rendelt modellek.
	 */
	// @Deprecated
	// private Map<Integer, Handler> modelServiceMap;

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
	 * @deprecated Use instead {@link ControllerServices} or
	 *             {@link ViewServices}!
	 */
	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ModelService {
	}

	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ControllerServices {
	}

	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ViewServices {
	}

	public final int getControllerServiceId(String serviceName)
			throws ServiceNotRegisteredException {
		if (this.controllerServiceIdMap.containsKey(serviceName)) {
			return this.controllerServiceIdMap.get(serviceName);
		} else {
			throw new ServiceNotRegisteredException(serviceName);
		}
	}

	public final int getViewServiceId(String serviceName)
			throws ServiceNotRegisteredException {
		if (this.viewServiceIdMap.containsKey(serviceName)) {
			return this.viewServiceIdMap.get(serviceName);
		} else {
			throw new ServiceNotRegisteredException(serviceName);
		}
	}

	/**
	 * A paraméterben megadott view handler feliratkoztatása a Presenterbe.
	 * 
	 * @param viewComponentCallback
	 * 
	 * @deprecated Használd inkább a
	 *             {@link Presenter#subscribeToServices(Callback, String...)}
	 *             metódust, mivel ezzel nem lehet megadni, hogy milyen
	 *             üzeneteket kapjon meg, így az új megoldásban nem kap semmit.
	 */
	public final void subscribe(Callback viewComponentCallback) {
		Handler handler = new Handler(viewComponentCallback);
		Log.d(TAG, "subscribe - add viewhandler: " + handler);
		this.viewHandlerMap.put(viewComponentCallback, handler);
		// this.viewHandlerList.add(handler);
	}

	/**
	 * Ugyanaz, mint a {@link #subscribe(Callback)}, csak ennél a metódusnál meg
	 * lehet adni konkrét szolgáltatásokat a view-oz.
	 * 
	 * @param viewComponentCallback
	 * @param viewServices
	 */
	public final void subscribeToServices(Callback viewComponentCallback,
			String... viewServices) {
		this.subscribe(viewComponentCallback);

		Log.d(TAG, "subscribeToServices - view: " + viewComponentCallback);

		for (String viewService : viewServices) {
			if (this.viewCallbacksMap.containsKey(viewService)) {
				List<Callback> viewCallbackList = this.viewCallbacksMap
						.remove(viewService);
				viewCallbackList.add(viewComponentCallback);
				this.viewCallbacksMap.put(viewService, viewCallbackList);
				Log.d(TAG, "subscribeToServices - viewService: " + viewService);
				Log.d(TAG, "subscribeToServices - callback add to service: " + viewComponentCallback);
				Log.d(TAG, "subscribeToServices - callback services: " + viewCallbackList.size());
			} else {
				throw new ServiceNotFoundException(viewService);
			}
		}
	}

	// TODO A map-ből nem törli még!
	/**
	 * Leregisztrálja a view-t a Presenterből.
	 * 
	 * @param viewComponentallback
	 */
	public final void unsubscribe(Callback viewComponentallback) {
		this.viewHandlerMap.remove(viewComponentallback);
	}

	// TODO Ezt ellenőrizd, mert már nem naprakész!
	public final void dispose() {
//		for (Handler handler : this.modelHandlerList) {
//			handler.getLooper().quit();
//		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}

	public Context getApplicationContext() {
		return this.context;
	}

	public void sendViewMessage(String serviceName)
			throws ServiceNotRegisteredException {
		this.sendViewMessage(this.getViewServiceId(serviceName), 0, 0, null,
				null);
	}

	public void sendViewMessage(String serviceName, int arg1)
			throws ServiceNotRegisteredException {
		this.sendViewMessage(this.getViewServiceId(serviceName), arg1, 0, null,
				null);
	}

	@Deprecated
	public void sendViewMessage(String serviceName, int arg1, MessageObject obj)
			throws ServiceNotRegisteredException {
		this.sendViewMessage(this.getViewServiceId(serviceName), arg1, 0, obj,
				null);
	}

	@Deprecated
	public void sendViewMessage(String serviceName, MessageObject obj)
			throws ServiceNotRegisteredException {
		this.sendViewMessage(this.getViewServiceId(serviceName), 0, 0, obj,
				null);
	}

	public void sendViewMessage(String serviceName, Object obj)
			throws ServiceNotRegisteredException {
		this.sendViewMessage(this.getViewServiceId(serviceName), 0, 0, obj,
				null);
	}

	public void sendViewMessage(String serviceName, Bundle bundle)
			throws ServiceNotRegisteredException {
		this.sendViewMessage(this.getViewServiceId(serviceName), 0, 0, null,
				bundle);
	}

	public void sendViewMessage(int what) {
		this.sendViewMessage(what, 0, 0, null, null);
	}

	public void sendViewMessage(int what, int arg1) {
		this.sendViewMessage(what, arg1, 0, null, null);
	}

	public void sendViewMessage(int what, int arg1, MessageObject obj) {
		this.sendViewMessage(what, arg1, 0, obj, null);
	}

	public void sendViewMessage(int what, MessageObject obj) {
		this.sendViewMessage(what, 0, 0, obj, null);
	}

	/**
	 * View üzenetet küld a {@link MessageObject#getModelName()}-től a
	 * {@link MessageObject#getSenderView()}-nak.
	 * 
	 * <br>
	 * 1.3: Küldj inkább {@link Parcelable} listát {@link Bundle}-n keresztül.
	 * Ha lehet ne használd a {@link MessageObject}-et.
	 * 
	 * @param what
	 * @param obj
	 *            Ha null vagy a sender adattag null, akkor mindenkinek küldi.
	 * 
	 */
	@Deprecated
	public final void sendViewMessage(int what, int arg1, int arg2,
			MessageObject obj) {
		String viewService = this.controllerServiceNameMap.get(what);
		Log.d(TAG, "sendViewMessage - viewService: " + viewService);

		if (obj.getSenderView() == null) {
			Log.d(TAG, "sendViewMessage - BROADCAST_MESSAGE");
		} else {
			Log.d(TAG, "sendViewMessage - send to concrate view");
		}

		sendMessageToTarget(this.viewHandlerMap.get(obj.getSenderView()), what,
				arg1, arg2, obj, null);
	}

	public final void sendViewMessage(int what, int arg1, int arg2, Object obj,
			Bundle bundle) {
		String viewService = this.viewServiceNameMap.get(what);

		List<Callback> callbackList = this.viewCallbacksMap.get(viewService);
		if (callbackList != null) {
			Log.d(TAG,
					"sendViewMessage - send message to " + callbackList.size()
							+ " view(s).");
			for (Callback callback : callbackList) {
				Log.d(TAG, "sendViewMessage - view: " + callback);
				Handler handler = this.viewHandlerMap.get(callback);

				Log.d(TAG, "subscribe - send handler: " + handler);
				sendMessageToTarget(handler, what, arg1, arg2, obj, bundle);
			}
		}
	}

	public void sendModelMessage(String serviceName)
			throws ServiceNotRegisteredException {
		this.sendModelMessage(this.getControllerServiceId(serviceName), 0, 0,
				null, null);
	}

	public void sendModelMessage(String serviceName, Bundle bundle)
			throws ServiceNotRegisteredException {
		this.sendModelMessage(this.getControllerServiceId(serviceName), 0, 0,
				null, bundle);
	}

	public void sendModelMessage(String serviceName, int arg, Bundle bundle)
			throws ServiceNotRegisteredException {
		this.sendModelMessage(this.getControllerServiceId(serviceName), arg, 0,
				null, bundle);
	}

	public void sendModelMessage(String serviceName, int arg, MessageObject obj)
			throws ServiceNotRegisteredException {
		this.sendModelMessage(this.getControllerServiceId(serviceName), arg, 0,
				obj, null);
	}

	public void sendModelMessage(String serviceName, MessageObject obj)
			throws ServiceNotRegisteredException {
		this.sendModelMessage(this.getControllerServiceId(serviceName), 0, 0,
				obj, null);
	}

	public void sendModelMessage(int what) {
		this.sendModelMessage(what, 0, 0, null, null);
	}

	public void sendModelMessage(int what, int arg, MessageObject obj) {
		this.sendModelMessage(what, arg, 0, obj, null);
	}

	public void sendModelMessage(int what, MessageObject obj) {
		this.sendModelMessage(what, 0, 0, obj, null);
	}

	public final void sendModelMessage(int what, int arg1, int arg2,
			MessageObject obj, Bundle bundle) {
		// Handler targetHandler = this.modelServiceMap.get(what);
		Handler targetHandler = this.controllerHandlerMap
				.get(this.controllerServiceNameMap.get(what));
		this.sendMessageToTarget(targetHandler, what, arg1, arg2, obj, bundle);
		Message.obtain(handler, what, arg1, arg2, obj).sendToTarget();
	}

	private Presenter(Context context, List<IController> controllerList) {
		this.context = context;
		Log.d(TAG, "Presenter - START");
		this.viewHandlerMap = new HashMap<Callback, Handler>();
		this.controllerHandlerMap = new HashMap<String, Handler>();

		// this.modelServiceMap = new HashMap<Integer, Handler>();

		this.controllerServiceIdMap = new HashMap<String, Integer>();
		this.controllerServiceNameMap = new HashMap<Integer, String>();
		this.viewServiceIdMap = new HashMap<String, Integer>();
		this.viewServiceNameMap = new HashMap<Integer, String>();

		this.viewCallbacksMap = new HashMap<String, List<Callback>>();

		// this.viewHandlerList = new ArrayList<Handler>();

		for (int i = 0; i < controllerList.size(); i++) {
			String controllerClassName = controllerList.get(i).getClass()
					.getName();

			// Init model handler.
			HandlerThread handlerThread = new HandlerThread(controllerClassName);
			handlerThread.start();
			Handler controllerHandler = new Handler(handlerThread.getLooper(),
					controllerList.get(i));

			// Associate model services to model handler.
			// this.fetchModelServices(controllerHandler,
			// controllerList.get(i));
			this.fetchControllerServices(controllerHandler,
					controllerList.get(i));
			this.fetchViewServices(controllerHandler, controllerList.get(i));

			// this.modelHandlerList.add(controllerHandler);
			// this.controllerHandlerMap.put(controllerClassName,
			// controllerHandler);
		}

		// TODO ezt a rész még nem vágom :S
		this.handlerThread = new HandlerThread(getClass().getCanonicalName());
		this.handlerThread.start();
		this.handler = new Handler(this.handlerThread.getLooper(), this);
	}

	private void fetchControllerServices(Handler controllerHandler,
			IController controller) {
		Log.i(TAG, "fetchControllerServices - START");
		Field[] fields = controller.getClass().getFields();
		for (int fieldIndex = 0; fieldIndex < fields.length; fieldIndex++) {
			if (fields[fieldIndex]
					.isAnnotationPresent(ControllerServices.class)) {
				try {
					String[] controllerServiceNames = (String[]) fields[fieldIndex]
							.get(null);
					for (String controllerServiceName : controllerServiceNames) {
						int serviceId = serviceIdCounter++;
						// this.modelServiceMap.put(serviceId,
						// controllerHandler);
						this.controllerServiceIdMap.put(controllerServiceName,
								serviceId);
						Log.i(TAG, "fetchControllerServices - serviceName: "
								+ controllerServiceName);
						this.controllerServiceNameMap.put(serviceId,
								controllerServiceName);
						this.controllerHandlerMap.put(controllerServiceName,
								controllerHandler);
					}
				} catch (IllegalArgumentException exception) {
					Log.w(TAG, "fetchControllerServices:", exception);
				} catch (IllegalAccessException exception) {
					Log.w(TAG, "fetchControllerServices:", exception);
				}
			}
		}
	}

	private void fetchViewServices(Handler modelHandler, IController controller) {
		Log.i(TAG, "fetchViewServices - START");
		Field[] fields = controller.getClass().getFields();
		for (int fieldIndex = 0; fieldIndex < fields.length; fieldIndex++) {
			if (fields[fieldIndex].isAnnotationPresent(ViewServices.class)) {
				try {
					String[] viewServiceNames = (String[]) fields[fieldIndex]
							.get(null);
					for (String viewServiceName : viewServiceNames) {
						int serviceId = serviceIdCounter++;
						// this.modelServiceMap.put(serviceId, modelHandler);
						this.viewServiceIdMap.put(viewServiceName, serviceId);
						this.viewCallbacksMap.put(viewServiceName,
								new LinkedList<Handler.Callback>());
						Log.i(TAG, "fetchViewServices - serviceName: "
								+ viewServiceName);
						this.viewServiceNameMap.put(serviceId, viewServiceName);
					}
				} catch (IllegalArgumentException exception) {
					Log.w(TAG, "fetchViewServices:", exception);
				} catch (IllegalAccessException exception) {
					Log.w(TAG, "fetchViewServices:", exception);
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
			throw new HandlerNotFoundException(what);
		}
	}

}
