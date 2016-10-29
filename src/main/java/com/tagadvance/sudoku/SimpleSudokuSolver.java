package com.tagadvance.sudoku;

import java.awt.Point;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;

public class SimpleSudokuSolver implements SudokuSolver {

	private int callCount;
	private long start;

	public SimpleSudokuSolver() {
		super();
	}

	@Override
	public Sudoku solve(Sudoku sudoku) throws UnsolvableException {
		Preconditions.checkNotNull(sudoku, "sudoku must not be null");
		sudoku = sudoku.copy();

		sudoku.validate();

		callCount = 0;
		start = System.currentTimeMillis();

		sudoku = sudoku.copy();
		try {
			return solve(sudoku, null);
		} finally {
			print();
		}
	}

	private void print() {
		System.out.println("callCount = " + callCount);
		double stop = System.currentTimeMillis();
		System.out.println((stop - start) / 1000 + " seconds");
	}

	private Sudoku solve(Sudoku sudoku, List<Point> cells) {
		if (sudoku.isSolved()) {
			return sudoku;
		}
		if (cells == null) {
			cells = getPrioritizedCellList(sudoku);
		}
		if (cells.isEmpty()) {
			return null;
		}
		callCount++;

		Point p = cells.remove(0);

		Set<String> potentialCellValues = sudoku.getCellPotentialValues(p.x, p.y);
		for (String value : potentialCellValues) {
			sudoku.setCellValue(p.x, p.y, value);
			Sudoku result = solve(sudoku, null);
			if (result != null) {
				return result;
			}
		}

		sudoku.setCellValue(p.x, p.y, Sudoku.EMPTY);
		cells.add(0, p);

		return null;
	}

	private List<Point> getPrioritizedCellList(Sudoku sudoku) {
		List<Point> emptyCells = sudoku.getEmptyCells();
		prioritize(sudoku, emptyCells);
		return emptyCells;
	}

	private Map<Point, Integer> prioritize(Sudoku sudoku, List<Point> emptyCells) {
		final Map<Point, Integer> cells = new HashMap<Point, Integer>();
		for (Point p : emptyCells) {
			Set<String> potentialValues = sudoku.getCellPotentialValues(p.x, p.y);
			int size = potentialValues.size();
			cells.put(p, size);
			potentialValues.clear();
		}
		// sort from least to greatest
		Collections.sort(emptyCells, new Comparator<Point>() {
			@Override
			public int compare(Point p1, Point p2) {
				int size1 = cells.get(p1);
				int size2 = cells.get(p2);
				return (size1 == size2 ? 0 : (size1 < size2 ? -1 : 1));
			}
		});
		return cells;
	}


}
