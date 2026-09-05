package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.Point;
import com.tagadvance.geometry.Rectangle;
import java.util.Objects;

class RectangleScope implements Scope {

	private final Rectangle rectangle;
	private final ImmutableSet<Point> pointSet;

	public RectangleScope(final Rectangle rectangle) {
		super();

		checkNotNull(rectangle, "rectangle must not be null");
		this.rectangle = new Rectangle(rectangle);
		this.pointSet = rectangle.stream().collect(ImmutableSet.toImmutableSet());
	}

	@Override
	public ImmutableSet<Point> getPoints() {
		return pointSet;
	}

	@Override
	public int hashCode() {
		return Objects.hash(rectangle, pointSet);
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof final RectangleScope that && Objects.equals(rectangle,
			that.rectangle) && Objects.equals(pointSet, that.pointSet);
	}

	@Override
	public String toString() {
		return rectangle.toString();
	}

}
