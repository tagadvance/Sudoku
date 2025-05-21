package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.tagadvance.geometry.Dimension;
import com.tagadvance.geometry.Point;
import java.util.Map.Entry;
import java.util.function.Function;

public class FixedSizeGrid<V> implements Grid<V> {

	public static final byte MIN_SIZE = 1, MAX_SIZE = 25;

	private final Dimension size;

	private final ImmutableMap<Point, Cell<V>> cellMap;

	public FixedSizeGrid(final Dimension size) {
		super();
		this.size = checkNotNull(size, "size must not be null");

		checkArgument(size.width() >= MIN_SIZE, "width must be >= %d", MIN_SIZE);
		checkArgument(size.width() <= MAX_SIZE, "width must be <= %d", MAX_SIZE);
		checkArgument(size.height() >= MIN_SIZE, "height must be >= %d", MIN_SIZE);
		checkArgument(size.height() <= MAX_SIZE, "height must be <= %d", MAX_SIZE);

		this.cellMap = size.stream()
			.collect(ImmutableMap.toImmutableMap(Function.identity(), p -> new MutableCell<>()));
	}

	private FixedSizeGrid(final Dimension size, final ImmutableMap<Point, Cell<V>> cellMap) {
		super();
		this.size = size;
		this.cellMap = cellMap;
	}

	@Override
	public FixedSizeGrid<V> copy() {
		final ImmutableMap<Point, Cell<V>> copy = cellMap.entrySet()
			.stream()
			.collect(ImmutableMap.toImmutableMap(Entry::getKey, e -> {
				final var value = e.getValue().getValue();

				return new MutableCell<>(value);
			}));

		return new FixedSizeGrid<>(size, copy);
	}

	@Override
	public Dimension getSize() {
		return this.size;
	}

	@Override
	public ImmutableCollection<Cell<V>> getCells() {
		return cellMap.values();
	}

	@Override
	public Cell<V> getCellAt(final Point point) {
		return cellMap.get(point);
	}

	// TODO: unit test
	@Override
	public String toString() {
		final var sb = new StringBuilder();
		for (int y = 0; y < size.height(); y++) {
			for (int x = 0; x < size.width(); x++) {
				Point point = new Point(x, y);
				Cell<V> cell = getCellAt(point);
				String value = cell.isEmpty() ? "?" : cell.getValue().toString();
				if (x > 0) {
					sb.append(" ");
				}
				sb.append(value);
			}
			if (y < size.height() - 1) {
				sb.append(StandardSystemProperty.LINE_SEPARATOR.value());
			}
		}

		return sb.toString();
	}

}
