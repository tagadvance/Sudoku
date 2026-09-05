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

public class FixedSizeGrid implements Grid {

	public static final byte MIN_SIZE = 1, MAX_SIZE = 25;

	/**
	 * How an empty cell prints. {@link Cell#EMPTY} itself is not printable.
	 */
	private static final char BLANK = '.';

	private final Dimension size;

	private final ImmutableMap<Point, Cell> cellMap;

	public FixedSizeGrid(final Dimension size) {
		super();
		this.size = checkNotNull(size, "size must not be null");

		checkArgument(size.width() >= MIN_SIZE, "width must be >= %s", MIN_SIZE);
		checkArgument(size.width() <= MAX_SIZE, "width must be <= %s", MAX_SIZE);
		checkArgument(size.height() >= MIN_SIZE, "height must be >= %s", MIN_SIZE);
		checkArgument(size.height() <= MAX_SIZE, "height must be <= %s", MAX_SIZE);

		this.cellMap = size.stream()
			.collect(ImmutableMap.toImmutableMap(Function.identity(), p -> new MutableCell()));
	}

	private FixedSizeGrid(final Dimension size, final ImmutableMap<Point, Cell> cellMap) {
		super();
		this.size = size;
		this.cellMap = cellMap;
	}

	@Override
	public FixedSizeGrid copy() {
		final ImmutableMap<Point, Cell> copy = cellMap.entrySet()
			.stream()
			.collect(ImmutableMap.toImmutableMap(Entry::getKey, e -> {
				final var value = e.getValue().getValue();

				return new MutableCell(value);
			}));

		return new FixedSizeGrid(size, copy);
	}

	@Override
	public Dimension getSize() {
		return this.size;
	}

	@Override
	public ImmutableCollection<Cell> getCells() {
		return cellMap.values();
	}

	@Override
	public Cell getCellAt(final Point point) {
		return cellMap.get(point);
	}

	@Override
	public String toString() {
		final var sb = new StringBuilder();
		for (int y = 0; y < size.height(); y++) {
			for (int x = 0; x < size.width(); x++) {
				final var cell = getCellAt(new Point(x, y));
				if (x > 0) {
					sb.append(" ");
				}
				sb.append(cell.isEmpty() ? BLANK : cell.getValue());
			}
			if (y < size.height() - 1) {
				sb.append(StandardSystemProperty.LINE_SEPARATOR.value());
			}
		}

		return sb.toString();
	}

//	class PointerCell implements Cell {
//
//		private final Point point;
//		private final int index;
//		private V value;
//
//		PointerCell(final Point p, final int index) {
//			this(p, index, null);
//		}
//
//		PointerCell(final Point p, final int index, final V value) {
//			this.point = p;
//			this.index = index;
//			this.value = value;
//		}
//
//		@Override
//		public boolean isEmpty() {
//			return this.value == null;
//		}
//
//		@Override
//		public V getValue() {
//			return this.value;
//		}
//
//		@Override
//		public void setValue(V value) {
//			this.value = value;
//		}
//
//	}


}
