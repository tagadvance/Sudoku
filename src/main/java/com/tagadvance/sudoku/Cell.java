package com.tagadvance.sudoku;

// TODO: replace with array reference
public interface Cell {

	/**
	 * The value of a cell that has not been filled in.
	 */
	char EMPTY = '0';

	boolean isEmpty();

	char getValue();

	void setValue(char value);

}
