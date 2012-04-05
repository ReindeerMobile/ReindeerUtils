package com.reindeermobile.reindeerutils.mvp;

import dalvik.system.DexFile;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class CoreApplication extends Application {
	public static final String TAG = "CoreApplication";

	private List<IController> modelList;
//	private List<IView> viewList;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate - START at " + System.currentTimeMillis());
		this.modelList = new ArrayList<IController>();

		this.loadModelClasses();

		// Presenter inicializálása
		Presenter.initInstance(getApplicationContext(), modelList);
		Log.i(TAG, "onCreate - OK");
	}
	
	private void loadModelClasses() {
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

//					Log.d(TAG, "loadModelClasses - check:" + appClass.getName());
					if (classContainsInterfaceByName(appClass, IController.class)) {
//						Log.d(TAG, "loadModelClasses - OK:" + appClass.getName());
						IController controller = (IController) appClass.newInstance();
//						model.init(this); // itt még nincs minden ControllerService felszedve.
						modelList.add(controller);
					}
				}
			}
		} catch (Throwable exception) {
			Log.e(TAG, "init", exception);
		}
	}
	
//	private void loadViewClasses() {
//		try {
//			ClassLoader loader = getClassLoader();
//			String packageName = getPackageName();
//			DexFile dexFile = new DexFile(getPackageManager()
//					.getApplicationInfo(getPackageName(), 0).sourceDir);
//			Enumeration<String> enumeration = dexFile.entries();
//
//			while (enumeration.hasMoreElements()) {
//				String className = enumeration.nextElement();
//				if (className.length() >= packageName.length()
//						&& packageName.equals(className.substring(0,
//								packageName.length()))) {
//					Class<?> appClass = loader.loadClass(className);
//
//					if (classContainsInterfaceByName(appClass, IView.class)) {
//						IView model = (IView) appClass.newInstance();
//						model.init(this);
//						modelList.add(model);
//					}
//				}
//			}
//		} catch (Throwable exception) {
//			Log.e(TAG, "init", exception);
//		}
//	}

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
			return classContainsInterfaceByName(clazz.getSuperclass(), classInterface);
		}
		return false;
	}

}
