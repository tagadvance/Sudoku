package com.tagadvance.sudoku;

// TODO: replace with array reference
public interface Cell {

	/**
	 * The value of a cell that has not been filled in. NUL is not a legal puzzle character, so no
	 * value alphabet can collide with it, and it is what a {@code char} field defaults to.
	 */
	char EMPTY = '\0';

	boolean isEmpty();

	char getValue();

	void setValue(char value);

}
