package com.reindeermobile.reindeerutils.mvp;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

//Nincs kész, ne használd!! Vagy akár be is fejezheted :D
@Deprecated
public class Wrapper {
//	private Class<T> clazz;
	private List<Object> list;
	
	public Wrapper(Type type, List<Object> list) {
		this.list = list;
	}
	
	public <T> List<T> getList(Class<T> clazz) throws SecurityException, NoSuchFieldException {
		Field field = Wrapper.class.getField("list");
		Type type = field.getGenericType();
		
		if (type instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType) type;
			for (Type argType : paramType.getActualTypeArguments()) {
				Class argClass = (Class) argType;
				
			}
		}
		return null;
	}
	
	public static void Main() {
		List<String> list = new ArrayList<String>();
//		Wrapper wrapper = new Wrapper(null, list);
		
//		Object obj = wrapper;
//		
//		List<String> destList = null;
//		if (obj instanceof Wrapper) {
//			wrapper = (Wrapper) obj;
//			list = wrapper.getList(String.class);
//		}
	}
	
}
