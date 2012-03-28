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
	 * @param bundle
	 *            Az intent, aminek paramétereket akarunk adni.
	 * @param obj
	 *            Ez tartalmazza a paramétereket.
	 * @param clazz
	 *            TODO
	 * @return Az intent-el tér vissza, ami már tartalmazza a paramétereket.
	 */
	public static <T> Intent putIntentParams(Intent intent, Object obj,
			Class<? super T> clazz) {
		if (intent == null) {
			throw new IllegalArgumentException("Intent is null!");
		}
		if (obj == null) {
			throw new IllegalArgumentException("Object is null!");
		}
		Bundle bundle = new Bundle();
		bundle = putBundleParams(bundle, obj, clazz);
		intent.putExtras(bundle);
		return intent;
	}

	public static <T> Bundle putBundleParams(Bundle bundle, Object obj,
			Class<? super T> clazz) {
		if (bundle == null) {
			throw new IllegalArgumentException("Bundle is null!");
		}
		if (obj == null) {
			throw new IllegalArgumentException("Object is null!");
		}

		if (clazz.getSuperclass() != Object.class) {
			bundle = putBundleParams(bundle, obj, clazz.getSuperclass());
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

				try {
					Method getter = clazz.getMethod(getterMethodName,
							new Class[] {});
					// Test reason only
					@SuppressWarnings("unused")
					Method setter = clazz.getMethod(setterMethodName,
							field.getType());
					Object value = getter.invoke(obj, new Object[] {});
					bundle = putBundleParam(bundle, name, value,
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
		return bundle;
	}

	public static <T> T parseIntent(Intent intent, Class<T> resultClass) {
//		Log.d(TAG, "parseIntent - START");
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
				result = parseBundle(bundle, result, resultClass);
			} catch (IllegalAccessException exception) {
				Log.w(TAG, "parseIntent - :", exception);
			} catch (InstantiationException exception) {
				Log.w(TAG, "parseIntent - :", exception);
			}

		return result;
	}

	public static <T> T parseBundle(Bundle bundle, T result,
			Class<? super T> resultClass) {
		if (resultClass.getSuperclass() != Object.class) {
			result = parseBundle(bundle, result, resultClass.getSuperclass());
		}

		if (bundle != null) {
			Field[] fields = resultClass.getDeclaredFields();
			for (Field field : fields) {
				try {
					if (field.isAnnotationPresent(IntentParam.class)) {
						IntentParam intentParam = field
								.getAnnotation(IntentParam.class);
						String name = intentParam.name();

						if (name.length() == 0) {
							name = field.getName();
						}

						String methodNamePostfix = field.getName()
								.substring(0, 1).toUpperCase()
								+ field.getName().substring(1);
						String setterMethodName = "set" + methodNamePostfix;

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

	private static Bundle putBundleParam(Bundle bundle, String name,
			Object value, Class<? extends Object> clazz) {
//		Log.d(TAG, "putBundleParam - name,class: " + name + "," + clazz);
		if (clazz == String.class) {
			bundle.putString(name, (String) value);
		} else if (clazz == int.class) {
			bundle.putInt(name, (Integer) value);
		} else if (clazz == Integer.class) {
			bundle.putInt(name, (Integer) value);
		} else if (clazz == long.class) {
			bundle.putLong(name, (Long) value);
		} else if (clazz == Long.class) {
			bundle.putLong(name, (Long) value);
		} else if (clazz == boolean.class) {
			bundle.putBoolean(name, (Boolean) value);
		}
		return bundle;
	}

	@SuppressWarnings("unused")
	private static Intent putIntentParam(Intent intent, String name,
			Object value, Class<? extends Object> clazz) {
//		Log.d(TAG, "putIntentParam - name,class: " + name + "," + clazz);
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
		return intent;
	}

}
