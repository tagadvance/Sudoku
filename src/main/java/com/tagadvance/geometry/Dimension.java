package com.tagadvance.geometry;

import com.google.common.base.MoreObjects;

public class Dimension implements ImmutableDimension {

	private final int width, height;

	public Dimension(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + width;
		result = prime * result + height;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass() != obj.getClass()) {
			return false;
		}
		Dimension other = (Dimension) obj;
		if (width != other.width) {
			return false;
		} else if (height != other.height) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(Dimension.class).add("width", width).add("height", height)
				.toString();
	}

}
