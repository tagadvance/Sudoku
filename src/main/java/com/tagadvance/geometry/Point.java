package com.tagadvance.geometry;

import com.google.common.base.MoreObjects;

public class Point implements ImmutablePoint {

	private final int x, y;

	public Point(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		Point other = (Point) obj;
		if (x != other.x) {
			return false;
		} else if (y != other.y) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(Point.class).add("x", x).add("y", y).toString();
	}

}
