package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.Point;
import com.tagadvance.geometry.Rectangle;
import java.util.Objects;

class RectangleScope<V> implements Scope<V> {

	private final Rectangle rectangle;
	private final ImmutableSet<Point> pointSet;

	/**
	 * cache of scopes for cell
	 */
	private final LoadingCache<Grid<V>, ImmutableCollection<Cell<V>>> cellCache = CacheBuilder.newBuilder()
		.build(new CacheLoader<>() {

			@Override
			public ImmutableCollection<Cell<V>> load(final Grid<V> grid) {
				return pointSet.stream()
					.map(grid::getCellAt)
					.collect(ImmutableList.toImmutableList());
			}

		});

	public RectangleScope(final Rectangle rectangle) {
		super();

		checkNotNull(rectangle, "rectangle must not be null");
		this.rectangle = new Rectangle(rectangle);
		this.pointSet = rectangle.stream().collect(ImmutableSet.toImmutableSet());
	}

	@Override
	public ImmutableCollection<Cell<V>> getCells(final Grid<V> grid) {
		return cellCache.getUnchecked(grid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(rectangle, pointSet);
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof final RectangleScope<?> that && Objects.equals(rectangle,
			that.rectangle) && Objects.equals(pointSet, that.pointSet);
	}

	@Override
	public String toString() {
		return rectangle.toString();
	}

}
