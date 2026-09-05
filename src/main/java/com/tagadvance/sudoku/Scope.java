package com.tagadvance.sudoku;

import com.google.common.primitives.ImmutableIntArray;

/**
 * A set of positions that must hold distinct values -- a row, a column, a block, and equally a
 * diagonal or an irregular cage. Pure geometry: it carries no values and knows no grid, so one
 * instance is shared by every grid and every copy of one.
 */
public interface Scope {

	/**
	 * Returns the positions this scope covers, as indices into a grid.
	 */
	ImmutableIntArray getPositions();

}
