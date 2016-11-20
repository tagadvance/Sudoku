package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.tagadvance.geometry.ImmutableDimension;
import com.tagadvance.geometry.ImmutablePoint;
import com.tagadvance.geometry.Point;

public class FixedSizeGrid<V> implements Grid<V> {

	public static final byte MIN_SIZE = 1, MAX_SIZE = 25;

	private final ImmutableDimension size;
	private final CellFactory<V> factory;
	
	private final ImmutableMap<ImmutablePoint, Cell<V>> cellMap;

	// TODO: unit test preconditions
	public FixedSizeGrid(ImmutableDimension size, CellFactory<V> factory) {
		super();
		this.size = checkNotNull(size, "size must not be null");
		this.factory = checkNotNull(factory, "factory must not be null");

		int width = size.getWidth(), height = size.getHeight();
		checkArgument(width >= MIN_SIZE, "width must be >= %d", MIN_SIZE);
		checkArgument(width <= MAX_SIZE, "width must be <= %d", MAX_SIZE);
		checkArgument(height >= MIN_SIZE, "height must be >= %d", MIN_SIZE);
		checkArgument(height <= MAX_SIZE, "height must be <= %d", MAX_SIZE);

		Map<ImmutablePoint, Cell<V>> pointMap = new HashMap<>();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ImmutablePoint point = new Point(x, y);
				Cell<V> cell = factory.createCell();
				pointMap.put(point, cell);
			}
		}
		this.cellMap = ImmutableMap.copyOf(pointMap);
	}
	
	private FixedSizeGrid(ImmutableDimension size, CellFactory<V> factory, ImmutableMap<ImmutablePoint, Cell<V>> cellMap) {
		super();
		this.size = size;
		this.factory = factory;
		
		Map<ImmutablePoint, Cell<V>> temporaryCellMap = new HashMap<>();
		for (Map.Entry<ImmutablePoint, Cell<V>> entry : cellMap.entrySet()) {
			ImmutablePoint point = entry.getKey();
			Cell<V> cell = entry.getValue();
			V value = cell.getValue();
			
			Cell<V> newCell = factory.createCell();
			newCell.setValue(value);
			
			temporaryCellMap.put(point, newCell);
		}
		this.cellMap = ImmutableMap.copyOf(temporaryCellMap);
	}

	@Override
	public FixedSizeGrid<V> copy() {
		return new FixedSizeGrid<>(size, factory, cellMap);
	}

	@Override
	public ImmutableDimension getSize() {
		return this.size;
	}

	@Override
	public ImmutableCollection<Cell<V>> getCells() {
		return cellMap.values();
	}

	@Override
	public Cell<V> getCellAt(ImmutablePoint point) {
		return cellMap.get(point);
	}

	// TODO: unit test
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < size.getHeight(); y++) {
			for (int x = 0; x < size.getWidth(); x++) {
				ImmutablePoint point = new Point(x, y);
				Cell<V> cell = getCellAt(point);
				String value = cell.isEmpty() ? "?" : cell.getValue().toString();
				if (x > 0) {
					sb.append(" ");
				}
				sb.append(value);
			}
			if (y < size.getHeight() - 1) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

}
