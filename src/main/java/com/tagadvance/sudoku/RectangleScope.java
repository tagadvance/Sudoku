package com.tagadvance.sudoku;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
class RectangleScope extends Rectangle implements Scope {

	private AbstractSudoku sudoku;
	private final Set<Point> cells;

	public RectangleScope(AbstractSudoku sudoku, int x, int y, int width, int height) {
		super(x, y, width, height);
		setSudoku(sudoku);

		Set<Point> cells = new HashSet<Point>();
		for (int y2 = y; y2 < y + height; y2++) {
			for (int x2 = x; x2 < x + width; x2++) {
				cells.add(new Point(x2, y2));
			}
		}
		this.cells = Collections.unmodifiableSet(cells);
	}

	@Override
	public boolean containsCell(Point p) {
		return this.cells.contains(p);
	}

	@Override
	public Set<Point> getCells() {
		return this.cells;
	}

	private void setSudoku(AbstractSudoku sudoku) {
		if (sudoku == null) {
			throw new IllegalArgumentException("sudoku must not be null");
		}
		this.sudoku = sudoku;
	}

	@Override
	public Set<Point> getEmptyCells() {
		Set<Point> set = new HashSet<Point>();
		for (Point cell : cells) {
			if (sudoku.isCellEmpty(cell.x, cell.y)) {
				set.add(new Point(cell));
			}
		}
		return set;
	}

	@Override
	public boolean isValid() {
		Set<String> set = new HashSet<String>();
		for (Point cell : cells) {
			String value = sudoku.getCellValue(cell.x, cell.y);
			if (sudoku.isEmpty(value) && !set.add(value)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void validate() throws UnsolvableException {
		Set<String> set = new HashSet<String>();
		for (Point cell : cells) {
			String value = sudoku.getCellValue(cell.x, cell.y);
			if (!sudoku.isEmpty(value) && !set.add(value)) {
				String message = "`" + value + "` @ [x = " + cell.x + ", y = " + cell.y + "]";
				throw new UnsolvableException(message);
			}
		}
	}

	@Override
	public boolean isSolved() {
		Set<String> set = new HashSet<String>();
		for (Point cell : cells) {
			String value = sudoku.getCellValue(cell.x, cell.y);
			if (sudoku.isEmpty(value) || !set.add(value)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Set<String> getUnusedValues() {
		Set<String> set = new HashSet<String>();
		String[] possibleValues = sudoku.getPossibleValues();
		for (String c : possibleValues) {
			set.add(c);
		}
		for (Point cell : cells) {
			String value = sudoku.getCellValue(cell.x, cell.y);
			set.remove(value);
		}
		return set;
	}

	@Override
	public String toString() {
		return getBounds().toString();
	}

}
