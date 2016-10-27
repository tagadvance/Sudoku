package com.tagadvance.sudoku;

import java.awt.Point;
import java.util.Set;

public interface Scope {

	boolean containsCell(Point p);

	Set<Point> getCells();

	Set<Point> getEmptyCells();

	Set<Character> getUnusedValues();

	boolean isValid();

	void validate() throws UnsolvableException;

	boolean isSolved();

}
