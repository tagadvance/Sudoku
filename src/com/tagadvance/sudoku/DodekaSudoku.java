package com.tagadvance.sudoku;

import java.util.HashSet;
import java.util.Set;

class DodekaSudoku extends Sudoku {

	public static final int DODEKA_WIDTH = 12, DODEKA_HEIGHT = 12;

	public DodekaSudoku() {
		super(DODEKA_WIDTH, DODEKA_HEIGHT);
	}

	public DodekaSudoku(Sudoku parent) {
		super(parent);
	}

	@Override
	protected char[] getPossibleValues() {
		return "0123456789ab".toCharArray();
	}

	@Override
	protected Set<Scope> createScopes() {
		Set<Scope> scopes = new HashSet<Scope>();

		// add blocks
		int horizontalRegions = 3, verticalRegions = 4, regionWidth = 4, regionHeight = 3;
		for (int y = 0, v = 0; v < verticalRegions; y += regionHeight, v++) {
			for (int x = 0, h = 0; h < horizontalRegions; x += regionWidth, h++) {
				RectangleScope scope = new RectangleScope(this, x, y, regionWidth, regionHeight);
				scopes.add(scope);
			}
		}
		assert scopes.size() == 12;

		// add columns
		int x = 0, y = 0;
		regionWidth = 1;
		for (x = 0; x < DODEKA_WIDTH; x++) {
			RectangleScope scope = new RectangleScope(this, x, y, regionWidth, DODEKA_HEIGHT);
			scopes.add(scope);
		}
		assert scopes.size() == 24;

		// add rows
		x = 0;
		y = 0;
		regionHeight = 1;
		for (y = 0; y < DODEKA_HEIGHT; y++) {
			RectangleScope scope = new RectangleScope(this, x, y, DODEKA_WIDTH, regionHeight);
			scopes.add(scope);
		}
		assert scopes.size() == 36;

		return scopes;
	}

	public Object clone() {
		return new DodekaSudoku(this);
	}

	public static Sudoku demo() {
		Sudoku sudoku = new DodekaSudoku();
		String puzzle = "?1????4??730" + "9??8?b?7????" + "67???0????b?" + "b?30a???1?7?"
				+ "5????3?4?a??" + "???7???6?3??" + "??8?1???b???" + "??4?6?a????3" + "?0?6???b94?8"
				+ "?9????3???21" + "????5?6?4??a" + "8ab??7????9?";
		return demo(sudoku, puzzle);
	}

}
