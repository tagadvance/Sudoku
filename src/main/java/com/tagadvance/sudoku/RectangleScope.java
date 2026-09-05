package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.primitives.ImmutableIntArray;
import com.tagadvance.geometry.Dimension;
import com.tagadvance.geometry.Rectangle;
import java.util.Objects;

class RectangleScope implements Scope {

	private final Rectangle rectangle;
	private final Dimension gridSize;
	private final ImmutableIntArray positions;

	RectangleScope(final Rectangle rectangle, final Dimension gridSize) {
		super();

		this.rectangle = new Rectangle(checkNotNull(rectangle, "rectangle must not be null"));
		this.gridSize = checkNotNull(gridSize, "gridSize must not be null");

		final var builder = ImmutableIntArray.builder(rectangle.width() * rectangle.height());
		rectangle.stream().forEach(point -> builder.add((point.y() * gridSize.width()) + point.x()));
		this.positions = builder.build();
	}

	@Override
	public ImmutableIntArray getPositions() {
		return positions;
	}

	@Override
	public int hashCode() {
		return Objects.hash(rectangle, gridSize);
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof final RectangleScope that && Objects.equals(rectangle, that.rectangle)
			&& Objects.equals(gridSize, that.gridSize);
	}

	@Override
	public String toString() {
		return rectangle.toString();
	}

}
