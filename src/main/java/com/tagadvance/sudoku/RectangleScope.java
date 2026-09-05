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

class RectangleScope implements Scope {

	private final Rectangle rectangle;
	private final ImmutableSet<Point> pointSet;

	/**
	 * Cache of the cells this scope covers, per grid. Keyed weakly so an entry dies with the grid
	 * copy it describes -- a solve discards thousands of them.
	 */
	private final LoadingCache<Grid, ImmutableCollection<Cell>> cellCache = CacheBuilder.newBuilder()
		.weakKeys()
		.build(new CacheLoader<>() {

			@Override
			public ImmutableCollection<Cell> load(final Grid grid) {
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
	public ImmutableCollection<Cell> getCells(final Grid grid) {
		return cellCache.getUnchecked(grid);
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
