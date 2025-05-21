package com.tagadvance.geometry;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Rectangle(int x, int y, int width, int height) {

	public Rectangle(final Rectangle rectangle) {
		this(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}

	public Point point() {
		return new Point(x, y);
	}

	public Dimension size() {
		return new Dimension(width, height);
	}

	public Stream<Point> stream() {
		return IntStream.range(y, y + height)
			.mapToObj(y -> IntStream.range(x, x + width).mapToObj(x -> new Point(x, y)))
			.flatMap(Function.identity());
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof final Rectangle rectangle && x == rectangle.x && y == rectangle.y
			&& width == rectangle.width && height == rectangle.height;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, width, height);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(Rectangle.class)
			.add("x", x)
			.add("y", y)
			.add("width", width)
			.add("height", height)
			.toString();
	}

}
