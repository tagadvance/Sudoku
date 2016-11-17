package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.tagadvance.geometry.ImmutableDimension;
import com.tagadvance.geometry.ImmutablePoint;
import com.tagadvance.geometry.Point;

public class FixedSizeGrid<V> implements Grid<V> {

	public static final byte MIN_SIZE = 1, MAX_SIZE = 25;

	private final ImmutableDimension size;
	private final CellFactory<V> factory;
	private Cell<V>[][] grid;

	// TODO: unit test preconditions
	@SuppressWarnings("unchecked")
	public FixedSizeGrid(ImmutableDimension size, CellFactory<V> factory) {
		super();
		this.size = checkNotNull(size, "size must not be null");
		this.factory = checkNotNull(factory, "factory must not be null");

		int width = size.getWidth(), height = size.getHeight();
		checkArgument(width >= MIN_SIZE, "width must be >= %d", MIN_SIZE);
		checkArgument(width <= MAX_SIZE, "width must be <= %d", MAX_SIZE);
		checkArgument(height >= MIN_SIZE, "height must be >= %d", MIN_SIZE);
		checkArgument(height <= MAX_SIZE, "height must be <= %d", MAX_SIZE);

		this.grid = new Cell[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				this.grid[y][x] = factory.createCell();
			}
		}
	}

	@Override
	public FixedSizeGrid<V> copy() {
		FixedSizeGrid<V> grid = new FixedSizeGrid<>(size, factory);
		for (int y = 0; y < this.grid.length; y++) {
			for (int x = 0; x < this.grid[y].length; x++) {
				V value = this.grid[y][x].getValue();
				grid.grid[y][x].setValue(value);
			}
		}
		return grid;
	}

	@Override
	public ImmutableDimension getSize() {
		return this.size;
	}

	@Override
	public Cell<V> getCellAt(ImmutablePoint point) {
		return this.grid[point.getY()][point.getX()];
	}

	// TODO: unit test
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[y].length; x++) {
				ImmutablePoint point = new Point(x, y);
				Cell<V> cell = getCellAt(point);
				String value = cell.isEmpty() ? "?" : cell.getValue().toString();
				if (x > 0) {
					sb.append(" ");
				}
				sb.append(value);
			}
			if (y < grid.length - 1) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

}
