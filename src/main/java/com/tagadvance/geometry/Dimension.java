package com.tagadvance.geometry;

import com.google.common.base.MoreObjects;
import org.jspecify.annotations.NonNull;

public record Dimension(int width, int height) {

	@Override
	@NonNull
	public String toString() {
		return MoreObjects.toStringHelper(Dimension.class)
			.add("width", width)
			.add("height", height)
			.toString();
	}

}
