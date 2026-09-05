package com.tagadvance.geometry;

import com.google.common.base.MoreObjects;
import org.jspecify.annotations.NonNull;

public record Point(int x, int y) {

	@Override
	@NonNull
	public String toString() {
		return MoreObjects.toStringHelper(Point.class).add("x", x).add("y", y).toString();
	}

}
