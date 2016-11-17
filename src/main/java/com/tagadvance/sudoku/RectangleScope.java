package com.tagadvance.sudoku;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.ImmutablePoint;
import com.tagadvance.geometry.Point;

@SuppressWarnings("serial")
class RectangleScope<V> extends Rectangle implements Scope<V> {

	private final ImmutableSet<ImmutablePoint> pointSet;

	public RectangleScope(int x, int y, int width, int height) {
		super(x, y, width, height);

		List<ImmutablePoint> pointList = new ArrayList<>();
		for (int y2 = y; y2 < y + height; y2++) {
			for (int x2 = x; x2 < x + width; x2++) {
				ImmutablePoint point = new Point(x2, y2);
				pointList.add(point);
			}
		}
		this.pointSet = ImmutableSet.copyOf(pointList);
	}

	// TODO: see if this is ever called
	@Override
	public ImmutableSet<ImmutablePoint> getCellPoints() {
		return this.pointSet;
	}

	@Override
	public Set<V> getUsedValues(Grid<V> grid) {
		Set<V> set = new HashSet<>();
		for (ImmutablePoint point : pointSet) {
			Cell<V> cell = grid.getCellAt(point);
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
		for (ImmutablePoint point : pointSet) {
			Cell<V> cell = grid.getCellAt(point);
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
		for (ImmutablePoint point : pointSet) {
			Cell<V> cell = grid.getCellAt(point);
			V value = cell.getValue();
			if (cell.isEmpty() || !set.add(value)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return getBounds().toString();
	}

}
