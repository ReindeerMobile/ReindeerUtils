package com.reindeermobile.reindeerutils.mvp;

import android.os.Handler.Callback;
import android.util.Log;

import java.lang.reflect.TypeVariable;

/**
 * Egy üzenetet reprezentál egy {@link #modelName}-el megadott model és egy
 * {@link #senderView} között. Az üzenet irányát a küldő metódus határozza meg.
 * 
 * @author zsdoma
 * 
 */
public class MessageObject {
	public static final String TAG = "MessageObject";

	private Callback senderView;
	private Object data;

	@Deprecated
	public static <T extends Object> T resolveMessageData(Object object,
			Class<T> clazz) {
		Log.d(TAG, "getMessageData - START");
		T data = null;
		TypeVariable<Class<T>>[] typeParameters = clazz.getTypeParameters();
		if (typeParameters.length == 1) {
			TypeVariable<Class<T>> typeParameter = typeParameters[0];
			data = typeParameter.getGenericDeclaration().cast(object);
		}
		Log.d(TAG, "getMessageData - END");
		return data;
	}

	public MessageObject(Object data) {
		this(null, data);
	}

	public MessageObject(Callback view) {
		this(view, null);
	}

	public MessageObject(Callback view, Object data) {
		super();
		this.senderView = view;
		this.data = data;
	}

	public final Callback getSenderView() {
		return this.senderView;
	}

	public final Object getData() {
		return this.data;
	}

	/**
	 * Ellenőrzni, hogy a {@link #getData()} clazz típusú-e. Ha az, akkor true
	 * egyébként false. Ha null, akkor szintén false.
	 * 
	 * @param clazz
	 * @return
	 */
	public final <T> boolean hasData(Class<T> clazz) {
		return (this.data != null && this.data.getClass() == clazz);
	}

	@Override
	public String toString() {
		return "MessageObject [senderView=" + this.senderView + ", data="
				+ this.data + "]";
	}

}
