package com.reindeermobile.reindeerutils.view.intent;

import android.content.Intent;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IntentUtils {
	public static final String TAG = "IntentUtils";

	/**
	 * A paraméterként kapott objektum {@link IntentParam} annotációval ellátott
	 * adattagjainak és metódusainak értékét hozzáadja az {@link Intent}-hez a
	 * {@link IntentParam#name()} -el megadott kulcsal, majd visszatér az új már
	 * kiegészített {@link Intent}-el.
	 * 
	 * @param intent
	 *            Az intent, aminek paramétereket akarunk adni.
	 * @param obj
	 *            Ez tartalmazza a paramétereket.
	 * @return Az intent-el tér vissza, ami már tartalmazza a paramétereket.
	 */
	public static Intent putIntentParams(Intent intent, Object obj) {
		if (intent == null) {
			throw new IllegalArgumentException("Intent is null!");
		}
		if (obj == null) {
			throw new IllegalArgumentException("Object is null!");
		}

		// Annotált mezők feldolgozása
		Field[] fields = obj.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (field.isAnnotationPresent(IntentParam.class)) {
				IntentParam ann = field.getAnnotation(IntentParam.class);
				String name = ann.name();
				String getterMethodName = "get"
						+ field.getName().substring(0, 1).toUpperCase()
						+ field.getName().substring(1);

				Object value = getMethodValue(getterMethodName, obj);
				intent = putIntentParam(intent, name, value, field.getType());
			}
		}

		// Annotált metódusok feldolgozása
		Method[] methods = obj.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];

			if (method.isAnnotationPresent(IntentParam.class)) {
				IntentParam ann = method.getAnnotation(IntentParam.class);
				String name = ann.name();
				Object value = getMethodValue(method.getName(), obj);
				intent = putIntentParam(intent, name, value,
						method.getReturnType());
			}

		}

		return intent;
	}

	private static Object getMethodValue(String methodName, Object obj) {
		Object value = null;
		try {
			Method method = obj.getClass()
					.getMethod(methodName, new Class[] {});
			value = method.invoke(obj, new Object[] {});
		} catch (SecurityException exception) {
			Log.w(TAG, "getMethodValue - ", exception);
		} catch (NoSuchMethodException exception) {
			Log.w(TAG, "getMethodValue - ", exception);
		} catch (IllegalArgumentException exception) {
			Log.w(TAG, "getMethodValue - ", exception);
		} catch (IllegalAccessException exception) {
			Log.w(TAG, "getMethodValue - ", exception);
		} catch (InvocationTargetException exception) {
			Log.w(TAG, "getMethodValue - ", exception);
		}
		return value;
	}

	private static Intent putIntentParam(Intent intent, String name,
			Object value, Class<? extends Object> clazz) {
		if (clazz == String.class) {
			intent.putExtra(name, (String) value);
		} else if (clazz == Integer.class) {
			intent.putExtra(name, (Integer) value);
		} else if (clazz == Boolean.class) {
			intent.putExtra(name, (Boolean) value);
		}
		return intent;
	}
}
