package com.tagadvance.geometry;

import com.google.common.base.MoreObjects;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.jspecify.annotations.NonNull;

public record Rectangle(int x, int y, int width, int height) {

	public Rectangle(final Rectangle rectangle) {
		this(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}

	@SuppressWarnings("unused")
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
	@NonNull
	public String toString() {
		return MoreObjects.toStringHelper(Rectangle.class)
			.add("x", x)
			.add("y", y)
			.add("width", width)
			.add("height", height)
			.toString();
	}

}
