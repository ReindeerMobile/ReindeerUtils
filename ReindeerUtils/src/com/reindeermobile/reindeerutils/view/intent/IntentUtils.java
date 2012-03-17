package com.reindeermobile.reindeerutils.view.intent;

import com.reindeermobile.reindeerutils.view.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Osztályok szétbontására és összerakására lehet használni, mikor át akarunk
 * küldeni példányokat két activity között.
 * 
 * @author zsdoma
 * 
 */
public class IntentUtils {
	public static final String TAG = "IntentUtils";

	/**
	 * Az {@link IntentUtils} használata esetén az ezzel az annotációval
	 * megjelölt adattagok be lesznek csomagolva.
	 * 
	 * @author zsdoma
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public @interface IntentParam {
		String name() default StringUtils.EMPTY_STRING;
	}

	/**
	 * A paraméterként kapott objektum {@link IntentParam} annotációval ellátott
	 * adattagjainak és metódusainak értékét hozzáadja az {@link Intent}-hez a
	 * {@link IntentParam#name()} -el megadott kulcsal, majd visszatér az új már
	 * kiegészített {@link Intent}-el.
	 * 
	 * @param <T>
	 * 
	 * @param intent
	 *            Az intent, aminek paramétereket akarunk adni.
	 * @param obj
	 *            Ez tartalmazza a paramétereket.
	 * @param clazz
	 *            TODO
	 * @return Az intent-el tér vissza, ami már tartalmazza a paramétereket.
	 */
	public static <T> Intent putIntentParams(Intent intent, Object obj,
			Class<? super T> clazz) {
		Log.d(TAG, "putIntentParams - START - " + clazz);
		if (intent == null) {
			throw new IllegalArgumentException("Intent is null!");
		}
		if (obj == null) {
			throw new IllegalArgumentException("Object is null!");
		}

		if (clazz.getSuperclass() != Object.class) {
			Log.d(TAG, "putIntentParams - call for supertype: " + clazz + " - "
					+ clazz.getSuperclass());
			intent = putIntentParams(intent, obj, clazz.getSuperclass());
		}

		// Annotált mezők feldolgozása
		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];

			if (field.isAnnotationPresent(IntentParam.class)) {
				IntentParam ann = field.getAnnotation(IntentParam.class);
				String name = ann.name();

				if (name.length() == 0) {
					name = field.getName();
				}

				String methodNamePostfix = field.getName().substring(0, 1)
						.toUpperCase()
						+ field.getName().substring(1);

				String getterMethodName = (field.getType() != boolean.class && field
						.getType() != Boolean.class) ? "get"
						+ methodNamePostfix : "is" + methodNamePostfix;
				String setterMethodName = "set" + methodNamePostfix;
				// Log.d(TAG, "putIntentParams - getterMethodName: "
				// + getterMethodName);
				// Log.d(TAG, "putIntentParams - setterMethodName: "
				// + setterMethodName);

				try {
					Method getter = clazz.getMethod(getterMethodName,
							new Class[] {});
					// Test reason only
					@SuppressWarnings("unused")
					Method setter = clazz.getMethod(setterMethodName,
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

		Log.d(TAG, "putIntentParams - END");
		return intent;
	}

	public static <T> T parseIntent(Intent intent, Class<T> resultClass) {
		Log.d(TAG, "parseIntent - START");
		if (intent == null) {
			throw new IllegalArgumentException("The intent is null!");
		}
		if (resultClass == null) {
			throw new IllegalArgumentException("The resultClass is null!");
		}

		T result = null;
		Bundle bundle = intent.getExtras();
		if (bundle != null)
			try {
				result = resultClass.newInstance();
				result = parseBundleToResultByClass(bundle, result, resultClass);
			} catch (IllegalAccessException exception) {
				Log.w(TAG, "parseIntent - :", exception);
			} catch (InstantiationException exception) {
				Log.w(TAG, "parseIntent - :", exception);
			}

		return result;
	}

	private static <T> T parseBundleToResultByClass(Bundle bundle, T result,
			Class<? super T> resultClass) {
		Log.d(TAG, "parseBundleToResultByClass - START - " + resultClass);
		if (resultClass.getSuperclass() != Object.class) {
			Log.d(TAG, "parseBundleToResultByClass - super: " + resultClass.getSuperclass());
			result = parseBundleToResultByClass(bundle, result,
					resultClass.getSuperclass());
		}

		if (bundle != null) {
			Field[] fields = resultClass.getDeclaredFields();
			for (Field field : fields) {
				try {
					if (field.isAnnotationPresent(IntentParam.class)) {
						IntentParam intentParam = field
								.getAnnotation(IntentParam.class);
						String name = intentParam.name();
						Log.d(TAG, "getIntentParams - name: " + name);

						if (name.length() == 0) {
							name = field.getName();
						}

						String methodNamePostfix = field.getName()
								.substring(0, 1).toUpperCase()
								+ field.getName().substring(1);
						String setterMethodName = "set" + methodNamePostfix;
						Log.d(TAG, "getIntentParams - " + setterMethodName);

						Method setter = resultClass.getMethod(setterMethodName,
								field.getType());

						Object value = null;
						if (field.getType() == String.class) {
							value = bundle.getString(name);
							setter.invoke(result, (String) value);
						} else if (field.getType() == boolean.class) {
							value = bundle.getBoolean(name);
							setter.invoke(result, (Boolean) value);
						} else if (field.getType() == int.class) {
							value = bundle.getInt(name);
							setter.invoke(result, (Integer) value);
						} else if (field.getType() == long.class) {
							value = bundle.getLong(name);
							setter.invoke(result, (Long) value);
						} else if (field.getType() == Integer.class) {
							value = bundle.getInt(name);
							setter.invoke(result, (Integer) value);
						} else if (field.getType() == Long.class) {
							value = bundle.getLong(name);
							setter.invoke(result, (Long) value);
						}
					}
				} catch (NoSuchMethodException exception) {
					Log.w(TAG, "parseBundleToResultByClass - :", exception);
				} catch (IllegalArgumentException exception) {
					Log.w(TAG, "parseBundleToResultByClass - :", exception);
				} catch (InvocationTargetException exception) {
					Log.w(TAG, "parseBundleToResultByClass - :", exception);
				} catch (IllegalAccessException exception) {
					Log.w(TAG, "parseBundleToResultByClass - :", exception);
				}
			}
		}
		return result;
	}

	// public static <T> T getIntentParams(Intent intent, Class<T> resultClass)
	// {
	// Log.d(TAG, "getIntentParams - START");
	// if (intent == null) {
	// throw new IllegalArgumentException("The intent is null!");
	// }
	// if (resultClass == null) {
	// throw new IllegalArgumentException("The resultClass is null!");
	// }
	//
	// T result = null;
	// try {
	// result = resultClass.newInstance();
	// Log.d(TAG, "getIntentParams - resultClass" + result.toString());
	// Bundle bundle = intent.getExtras();
	// if (bundle != null) {
	// Field[] fields = resultClass.getDeclaredFields();
	// for (Field field : fields) {
	// try {
	// if (field.isAnnotationPresent(IntentParam.class)) {
	// IntentParam intentParam = field
	// .getAnnotation(IntentParam.class);
	// String name = intentParam.name();
	// Log.d(TAG, "getIntentParams - name: " + name);
	//
	// if (name.length() == 0) {
	// name = field.getName();
	// }
	//
	// String methodNamePostfix = field.getName()
	// .substring(0, 1).toUpperCase()
	// + field.getName().substring(1);
	// String setterMethodName = "set" + methodNamePostfix;
	// Log.d(TAG, "getIntentParams - " + setterMethodName);
	//
	// Method setter = resultClass.getMethod(
	// setterMethodName, field.getType());
	//
	// Object value = null;
	// if (field.getType() == String.class) {
	// value = bundle.getString(name);
	// setter.invoke(result, (String) value);
	// } else if (field.getType() == boolean.class) {
	// value = bundle.getBoolean(name);
	// setter.invoke(result, (Boolean) value);
	// } else if (field.getType() == int.class) {
	// value = bundle.getInt(name);
	// setter.invoke(result, (Integer) value);
	// } else if (field.getType() == long.class) {
	// value = bundle.getLong(name);
	// setter.invoke(result, (Long) value);
	// } else if (field.getType() == Integer.class) {
	// value = bundle.getInt(name);
	// setter.invoke(result, (Integer) value);
	// } else if (field.getType() == Long.class) {
	// value = bundle.getLong(name);
	// setter.invoke(result, (Long) value);
	// }
	// }
	// } catch (NoSuchMethodException exception) {
	// Log.w(TAG, "getIntentParams - :", exception);
	// } catch (IllegalArgumentException exception) {
	// Log.w(TAG, "getIntentParams - :", exception);
	// } catch (InvocationTargetException exception) {
	// Log.w(TAG, "getIntentParams - :", exception);
	// }
	// }
	// }
	// } catch (SecurityException exception) {
	// Log.w(TAG, "getMethodValue - ", exception);
	// } catch (IllegalAccessException exception) {
	// Log.w(TAG, "getMethodValue - ", exception);
	// } catch (InstantiationException exception) {
	// Log.w(TAG, "getMethodValue - ", exception);
	// }
	//
	// Log.d(TAG, "getIntentParams - END");
	//
	// return result;
	// }

	private static Intent putIntentParam(Intent intent, String name,
			Object value, Class<? extends Object> clazz) {
		Log.d(TAG, "putIntentParam - START");
		if (clazz == String.class) {
			intent.putExtra(name, (String) value);
		} else if (clazz == int.class) {
			intent.putExtra(name, (Integer) value);
		} else if (clazz == Integer.class) {
			intent.putExtra(name, (Integer) value);
		} else if (clazz == long.class) {
			intent.putExtra(name, (Long) value);
		} else if (clazz == Long.class) {
			intent.putExtra(name, (Long) value);
		} else if (clazz == boolean.class) {
			intent.putExtra(name, (Boolean) value);
		}
		Log.d(TAG, "putIntentParam - END");
		return intent;
	}

}
