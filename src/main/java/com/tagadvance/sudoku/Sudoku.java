package com.tagadvance.sudoku;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import java.util.Set;

public interface Sudoku extends Copyable<Sudoku> {

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	String getCellValue(int x, int y);

	void setCellValue(int x, int y, String value);

	Dimension getSize();

	Set<Scope> getScopes();

	Set<Scope> getScopesForCell(int x, int y);

	/**
	 * I would like to optimize this by calculating the potential values for every cell which shares
	 * a scope with the set cell when setCellValue is called
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	Set<String> getCellPotentialValues(int x, int y);

	void clear();

	List<Point> getEmptyCells();

	boolean isCellEmpty(int x, int y);

	boolean isEmpty(String value);

	boolean isValid();

	void validate() throws UnsolvableException;

	boolean isSolved();

}
