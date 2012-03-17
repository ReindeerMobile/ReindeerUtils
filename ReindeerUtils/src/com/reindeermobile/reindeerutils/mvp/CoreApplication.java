package com.reindeermobile.reindeerutils.mvp;

import dalvik.system.DexFile;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class CoreApplication extends Application {
	public static final String TAG = "CoreApplication";

	private List<IModel> modelList;

	@Override
	public void onCreate() {
		super.onCreate();

		this.modelList = new ArrayList<IModel>();

		this.loadModelClasses();

		// Presenter inicializálása
		Presenter.initInstance(getApplicationContext(), modelList);
	}

	private void loadModelClasses() {
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

					if (classContainsInterfaceByName(appClass, IModel.class)) {
						IModel model = (IModel) appClass.newInstance();
						model.init(this);
						modelList.add(model);
					}
				}
			}
		} catch (Throwable exception) {
			Log.e(TAG, "init", exception);
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
		return false;
	}

}
