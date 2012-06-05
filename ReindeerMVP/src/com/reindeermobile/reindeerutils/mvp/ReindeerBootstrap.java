package com.reindeermobile.reindeerutils.mvp;

import dalvik.system.DexFile;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * <code>ReindeerBootstrap.init(this)</code> - if this is extend the Application class.
 * 
 * @author zsdoma
 *
 */
public class ReindeerBootstrap {
	public static final String TAG = "ReindeerBootstrap";

	private static boolean started;

	public static void init(Application application) {
		if (!started) {
			List<IController> controllerList = new ArrayList<IController>();

			loadControllerClasses(application, controllerList);

			Presenter.initInstance(application.getApplicationContext(),
					controllerList);

			started = true;
		}
	}

	private static void loadControllerClasses(Application application,
			List<IController> controllerList) {
		Log.d(TAG, "loadControllerClasses - START");
		try {
			ClassLoader loader = application.getClassLoader();
			String packageName = application.getPackageName();
			DexFile dexFile = new DexFile(
					application.getPackageManager().getApplicationInfo(
							application.getPackageName(), 0).sourceDir);
			Enumeration<String> enumeration = dexFile.entries();

			while (enumeration.hasMoreElements()) {
				String className = enumeration.nextElement();
				if (className.length() >= packageName.length()
						&& packageName.equals(className.substring(0,
								packageName.length()))) {
					Class<?> appClass = loader.loadClass(className);

					if (ReindeerBootstrap.classContainsInterfaceByName(
							appClass, IController.class)) {
						IController controller = (IController) appClass
								.newInstance();
						controllerList.add(controller);
					}
				}
			}
		} catch (Throwable exception) {
			Log.e(TAG, "loadControllerClasses - ", exception);
		}
	}

	static boolean classContainsInterfaceByName(Class<?> clazz,
			Class<?> classInterface) {
		Class<?>[] interfaces = clazz.getInterfaces();
		for (@SuppressWarnings("rawtypes")
		Class interfaze : interfaces) {
			if ((interfaze).equals(classInterface)) {
				return true;
			}
		}
		if (clazz.getSuperclass() != null) {
			return classContainsInterfaceByName(clazz.getSuperclass(),
					classInterface);
		}
		return false;
	}

}
