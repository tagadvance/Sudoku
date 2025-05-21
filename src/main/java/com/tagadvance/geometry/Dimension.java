package com.tagadvance.geometry;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Dimension(int width, int height) {

	public Stream<Point> stream() {
		return IntStream.range(0, height)
			.mapToObj(y -> IntStream.range(0, width).mapToObj(x -> new Point(x, y)))
			.flatMap(Function.identity());
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof final Dimension dimension && width == dimension.width
			&& height == dimension.height;
	}

	@Override
	public int hashCode() {
		return Objects.hash(width, height);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(Dimension.class)
			.add("width", width)
			.add("height", height)
			.toString();
	}

}
