package com.tagadvance.geometry;

import com.google.common.base.MoreObjects;
import java.util.Objects;

public record Point(int x, int y) {

	@Override
	public boolean equals(final Object o) {
		return o instanceof final Point point && x == point.x && y == point.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(Point.class).add("x", x).add("y", y).toString();
	}

}
