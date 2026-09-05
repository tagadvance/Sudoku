package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.StandardSystemProperty;
import com.tagadvance.geometry.Dimension;

public class FixedSizeGrid implements Grid {

	public static final byte MIN_SIZE = 1, MAX_SIZE = 25;

	/**
	 * How an empty position prints. {@link Grid#EMPTY} itself is not printable.
	 */
	private static final char BLANK = '.';

	private final Dimension size;

	private final char[] cells;

	public FixedSizeGrid(final Dimension size) {
		super();
		this.size = checkNotNull(size, "size must not be null");

		checkArgument(size.width() >= MIN_SIZE, "width must be >= %s", MIN_SIZE);
		checkArgument(size.width() <= MAX_SIZE, "width must be <= %s", MAX_SIZE);
		checkArgument(size.height() >= MIN_SIZE, "height must be >= %s", MIN_SIZE);
		checkArgument(size.height() <= MAX_SIZE, "height must be <= %s", MAX_SIZE);

		// char[] is born full of '\0', which is EMPTY
		this.cells = new char[size.width() * size.height()];
	}

	private FixedSizeGrid(final Dimension size, final char[] cells) {
		super();
		this.size = size;
		this.cells = cells;
	}

	@Override
	public FixedSizeGrid copy() {
		return new FixedSizeGrid(size, cells.clone());
	}

	@Override
	public Dimension getSize() {
		return size;
	}

	@Override
	public int size() {
		return cells.length;
	}

	@Override
	public char get(final int position) {
		return cells[position];
	}

	@Override
	public void set(final int position, final char value) {
		cells[position] = value;
	}

	@Override
	public String toString() {
		final var sb = new StringBuilder();
		for (int y = 0; y < size.height(); y++) {
			for (int x = 0; x < size.width(); x++) {
				final char value = cells[(y * size.width()) + x];
				if (x > 0) {
					sb.append(" ");
				}
				sb.append(value == EMPTY ? BLANK : value);
			}
			if (y < size.height() - 1) {
				sb.append(StandardSystemProperty.LINE_SEPARATOR.value());
			}
		}

		return sb.toString();
	}

}
