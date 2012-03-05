package com.reindeermobile.reindeerutils.db;

import com.reindeermobile.reindeerutils.db.DbAdapterFactory.Column;

public class BaseDbEntity {

	@Column(name = "_id")
	protected long id;

	public final long getId() {
		return this.id;
	}

	public final void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "BaseDbEntity [id=" + this.id + "]";
	}

}
