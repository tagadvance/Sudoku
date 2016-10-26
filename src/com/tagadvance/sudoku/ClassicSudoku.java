package com.tagadvance.sudoku;

import java.util.HashSet;
import java.util.Set;

class ClassicSudoku extends Sudoku {

	public static final int CLASSIC_WIDTH = 9, CLASSIC_HEIGHT = 9;

	public ClassicSudoku() {
		super(CLASSIC_WIDTH, CLASSIC_HEIGHT);
	}

	public ClassicSudoku(Sudoku parent) {
		super(parent);
	}

	protected Set<Scope> createScopes() {
		Set<Scope> scopes = new HashSet<Scope>();

		// add blocks
		int horizontalRegions = 3, verticalRegions = 3, regionWidth = 3, regionHeight = 3;
		for (int y = 0, v = 0; v < verticalRegions; y += regionHeight, v++) {
			for (int x = 0, h = 0; h < horizontalRegions; x += regionWidth, h++) {
				RectangleScope scope = new RectangleScope(this, x, y, regionWidth, regionHeight);
				scopes.add(scope);
			}
		}

		// add columns
		int x = 0, y = 0;
		regionWidth = 1;
		for (x = 0; x < CLASSIC_WIDTH; x++) {
			RectangleScope scope = new RectangleScope(this, x, y, regionWidth, CLASSIC_HEIGHT);
			scopes.add(scope);
		}

		// add rows
		x = 0;
		y = 0;
		regionHeight = 1;
		for (y = 0; y < CLASSIC_HEIGHT; y++) {
			RectangleScope scope = new RectangleScope(this, x, y, CLASSIC_WIDTH, regionHeight);
			scopes.add(scope);
		}

		return scopes;
	}

	public char[] getPossibleValues() {
		return "123456789".toCharArray();
	}

	public Object clone() {
		return new ClassicSudoku(this);
	}

}