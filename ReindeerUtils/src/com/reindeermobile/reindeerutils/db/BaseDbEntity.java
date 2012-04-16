package com.reindeermobile.reindeerutils.db;

import com.reindeermobile.reindeerutils.db.DbAdapterFactory.Column;
import com.reindeermobile.reindeerutils.db.DbAdapterFactory.Id;
import com.reindeermobile.reindeerutils.view.intent.IntentUtils.IntentParam;

public class BaseDbEntity {

	@Id
	@Column(name = "_id")
	@IntentParam
	protected long id;

	public final long getId() {
		return this.id;
	}

	public final void setId(long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.id ^ (this.id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BaseDbEntity other = (BaseDbEntity) obj;
		if (this.id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "BaseDbEntity [id=" + this.id + "]";
	}

}
