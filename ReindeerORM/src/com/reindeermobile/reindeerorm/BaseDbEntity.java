package com.reindeermobile.reindeerorm;

import com.reindeermobile.reindeerorm.annotations.Column;
import com.reindeermobile.reindeerorm.annotations.Id;
import com.reindeermobile.reindeerutils.view.intent.IntentUtils.IntentParam;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseDbEntity implements Parcelable {
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.id);
	}

}
