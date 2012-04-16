package com.reindeermobile.reindeerutils.mvp;

import java.util.List;

public class ListWrapper<T> {
	private List<T> list;

	public ListWrapper(List<T> list) {
		super();
		this.list = list;
	}

	public final List<T> getList() {
		return this.list;
	}

}
