package com.reindeermobile.reindeerutils.view.intent;

import android.content.Intent;
import android.os.Bundle;
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
		Log.d(TAG, "putIntentParams - START");
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

				if (name == null || name.length() == 0) {
					name = field.getName();
				}

				String methodNamePostfix = field.getName().substring(0, 1)
						.toUpperCase()
						+ field.getName().substring(1);
				String getterMethodName = "get" + methodNamePostfix;
				String setterMethodName = "set" + methodNamePostfix;

				try {
					Method getter = obj.getClass().getMethod(getterMethodName,
							new Class[] {});
					@SuppressWarnings("unused")
					// Test reason only
					Method setter = obj.getClass().getMethod(setterMethodName,
							field.getType());

					Object value = getter.invoke(obj, new Object[] {});
					intent = putIntentParam(intent, name, value,
							field.getType());
				} catch (SecurityException exception) {
					Log.e(TAG, "putIntentParams - ", exception);
				} catch (IllegalArgumentException exception) {
					Log.e(TAG, "putIntentParams - ", exception);
					throw exception;
				} catch (NoSuchMethodException exception) {
					Log.w(TAG, "putIntentParams - ", exception);
				} catch (IllegalAccessException exception) {
					Log.e(TAG, "putIntentParams - ", exception);
				} catch (InvocationTargetException exception) {
					Log.e(TAG, "putIntentParams - ", exception);
				}
			}
		}

		/*
		 * Nem kell, mert kötelező a getter-setter az adattaghoz, így elég az
		 * adattagból kiindulva meghívni a metódusokat.
		 */
		/*
		 * Annotált metódusok feldolgozása
		 */
		// Method[] methods = obj.getClass().getMethods();
		// for (int i = 0; i < methods.length; i++) {
		// Method method = methods[i];
		//
		// String fieldName =
		//
		// if (method.isAnnotationPresent(IntentParam.class) &&
		// method.getName().startsWith("get")) {
		// IntentParam ann = method.getAnnotation(IntentParam.class);
		// String name = ann.name();
		// Object value = getMethodValue(method.getName(), obj);
		// intent = putIntentParam(intent, name, value,
		// method.getReturnType());
		// }
		//
		// }

		Log.d(TAG, "putIntentParams - END");
		return intent;
	}
	
	public static <T> T getIntentParams(Intent intent, Class<T> resultClass) {
		Log.d(TAG, "getIntentParams - START");
		if (intent == null) {
			throw new IllegalArgumentException("The intent is null!");
		}
		if (resultClass == null) {
			throw new IllegalArgumentException("The resultClass is null!");
		}
		
		T result = null;
		try {
			result = resultClass.newInstance();
			Log.d(TAG, "getIntentParams - resultClass" + result.toString());
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Field[] fields = resultClass.getDeclaredFields();
				for (Field field : fields) {
					try {
						if (field.isAnnotationPresent(IntentParam.class)) {
							IntentParam intentParam = field
									.getAnnotation(IntentParam.class);
							String name = intentParam.name();

							if (name == null || name.length() == 0) {
								name = field.getName();
							}

							String methodNamePostfix = field.getName()
									.substring(0, 1).toUpperCase()
									+ field.getName().substring(1);
							String setterMethodName = "set" + methodNamePostfix;

							Method setter = resultClass.getMethod(
									setterMethodName, field.getType());

							Object value = null;
							if (field.getType() == String.class) {
								value = bundle.getString(name);
								setter.invoke(result, value);
							}
						}
					} catch (NoSuchMethodException exception) {
						Log.w(TAG, "getIntentParams - :", exception);
					} catch (IllegalArgumentException exception) {
						Log.w(TAG, "getIntentParams - :", exception);
					} catch (InvocationTargetException exception) {
						Log.w(TAG, "getIntentParams - :", exception);
					}
				}
			}
		} catch (SecurityException exception) {
			Log.w(TAG, "getMethodValue - ", exception);
		} catch (IllegalAccessException exception) {
			Log.w(TAG, "getMethodValue - ", exception);
		} catch (InstantiationException exception) {
			Log.w(TAG, "getMethodValue - ", exception);
		}
		
		Log.d(TAG, "getIntentParams - END");

		return result;
	}

	private static Intent putIntentParam(Intent intent, String name,
			Object value, Class<? extends Object> clazz) {
		Log.d(TAG, "putIntentParam - START");
		if (clazz == String.class) {
			intent.putExtra(name, (String) value);
		} else if (clazz == Integer.class) {
			intent.putExtra(name, (Integer) value);
		} else if (clazz == Boolean.class) {
			intent.putExtra(name, (Boolean) value);
		}
		Log.d(TAG, "putIntentParam - END");
		return intent;
	}
}
