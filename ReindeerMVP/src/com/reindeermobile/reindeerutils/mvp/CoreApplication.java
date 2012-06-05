package com.reindeermobile.reindeerutils.mvp;

import dalvik.system.DexFile;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Deprecated
public class CoreApplication extends Application {
	public static final String TAG = "CoreApplication";

	private List<IController> controllerList;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate - START at " + System.currentTimeMillis());

		this.controllerList = new ArrayList<IController>();

		this.loadControllerClasses();

		// Presenter inicializálása
		Presenter.initInstance(getApplicationContext(), controllerList);
		Log.i(TAG, "onCreate - OK");
	}

	private void loadControllerClasses() {
		Log.d(TAG, "loadModelClasses - START");
		try {
			ClassLoader loader = getClassLoader();
			String packageName = getPackageName();
			DexFile dexFile = new DexFile(getPackageManager()
					.getApplicationInfo(getPackageName(), 0).sourceDir);
			Enumeration<String> enumeration = dexFile.entries();

			while (enumeration.hasMoreElements()) {
				String className = enumeration.nextElement();
				if (className.length() >= packageName.length()
						&& packageName.equals(className.substring(0,
								packageName.length()))) {
					Class<?> appClass = loader.loadClass(className);

					// Log.d(TAG, "loadModelClasses - check:" +
					// appClass.getName());
					if (ReindeerBootstrap.classContainsInterfaceByName(
							appClass, IController.class)) {
						// Log.d(TAG, "loadModelClasses - OK:" +
						// appClass.getName());
						IController controller = (IController) appClass
								.newInstance();
						// model.init(this); // itt még nincs minden
						// ControllerService felszedve.
						controllerList.add(controller);
					}
				}
			}
		} catch (Throwable exception) {
			Log.e(TAG, "init", exception);
		}
	}

}
