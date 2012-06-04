package com.reindeermobile.reindeerorm;

import java.util.List;

public interface Query<T> {
	List<T> list();
	T uniqueEntity();
	void execute();
	
	void setParameter(String name, Object value);
	Object getParameter(String name);
}
