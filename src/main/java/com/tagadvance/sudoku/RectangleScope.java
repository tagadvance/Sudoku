package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.ImmutablePoint;
import com.tagadvance.geometry.Point;

class RectangleScope<V> implements Scope<V> {

	private final Rectangle rectangle;
	private final ImmutableSet<ImmutablePoint> pointSet;

	/**
	 * cache of scopes for cell
	 */
	private final LoadingCache<Grid<V>, ImmutableCollection<Cell<V>>> cellCache =
			CacheBuilder.newBuilder().build(new CellCacheLoader());

	public RectangleScope(Rectangle rectangle) {
		super();

		// beautiful error message
		checkNotNull(rectangle, "rectangle must not be null");
		this.rectangle = new Rectangle(rectangle);

		List<ImmutablePoint> pointList = new ArrayList<>();
		for (int y2 = rectangle.y; y2 < rectangle.y + rectangle.height; y2++) {
			for (int x2 = rectangle.x; x2 < rectangle.x + rectangle.width; x2++) {
				ImmutablePoint point = new Point(x2, y2);
				pointList.add(point);
			}
		}
		this.pointSet = ImmutableSet.copyOf(pointList);
	}

	@Override
	public ImmutableCollection<Cell<V>> getCells(Grid<V> grid) {
		try {
			return cellCache.get(grid);
		} catch (ExecutionException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public List<V> getUsedValues(Grid<V> grid) {
		List<V> set = new ArrayList<>();
		for (Cell<V> cell : getCells(grid)) {
			if (!cell.isEmpty()) {
				V value = cell.getValue();
				set.add(value);
			}
		}
		return set;
	}

	@Override
	public boolean isValid(Grid<V> grid) {
		Set<V> set = new HashSet<>();
		for (Cell<V> cell : getCells(grid)) {
			if (cell.isEmpty()) {
				continue;
			}

			V value = cell.getValue();
			if (!set.add(value)) {
				System.err.printf("%s could not be added to set%n", value);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isSolved(Grid<V> grid) {
		Set<V> set = new HashSet<>();
		for (Cell<V> cell : getCells(grid)) {
			V value = cell.getValue();
			if (cell.isEmpty() || !set.add(value)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(rectangle, pointSet);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		RectangleScope other = (RectangleScope) obj;
		return Objects.equals(rectangle, other.rectangle)
				&& Objects.equals(pointSet, other.pointSet);
	}

	@Override
	public String toString() {
		return rectangle.toString();
	}

	private class CellCacheLoader extends CacheLoader<Grid<V>, ImmutableCollection<Cell<V>>> {

		@Override
		public ImmutableCollection<Cell<V>> load(Grid<V> grid) throws Exception {
			List<Cell<V>> cellList = new ArrayList<>();
			for (ImmutablePoint point : pointSet) {
				Cell<V> cell = grid.getCellAt(point);
				cellList.add(cell);
			}
			return ImmutableList.copyOf(cellList);
		}

	}

}
