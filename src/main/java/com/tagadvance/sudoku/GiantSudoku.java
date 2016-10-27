package com.tagadvance.sudoku;

import java.util.HashSet;
import java.util.Set;

class GiantSudoku extends AbstractSudoku {

	public static final int GIANT_WIDTH = 25, GIANT_HEIGHT = 25;

	public GiantSudoku() {
		super(GIANT_WIDTH, GIANT_HEIGHT);
	}

	public GiantSudoku(AbstractSudoku parent) {
		super(parent);
	}

	protected Set<Scope> createScopes() {
		Set<Scope> scopes = new HashSet<Scope>();

		// add blocks
		int horizontalRegions = 5, verticalRegions = 5, regionWidth = 5, regionHeight = 5;
		for (int y = 0, v = 0; v < verticalRegions; y += regionHeight, v++) {
			for (int x = 0, h = 0; h < horizontalRegions; x += regionWidth, h++) {
				RectangleScope scope = new RectangleScope(this, x, y, regionWidth, regionHeight);
				scopes.add(scope);
			}
		}
		assert scopes.size() == 25;

		// add columns
		int x = 0, y = 0;
		regionWidth = 1;
		for (x = 0; x < GIANT_WIDTH; x++) {
			RectangleScope scope = new RectangleScope(this, x, y, regionWidth, GIANT_HEIGHT);
			scopes.add(scope);
		}
		assert scopes.size() == 50;

		// add rows
		x = 0;
		y = 0;
		regionHeight = 1;
		for (y = 0; y < GIANT_HEIGHT; y++) {
			RectangleScope scope = new RectangleScope(this, x, y, GIANT_WIDTH, regionHeight);
			scopes.add(scope);
		}
		assert scopes.size() == 75;

		return scopes;
	}

	@Override
	protected char[] getPossibleValues() {
		return "123456789abcdefg".toCharArray();
	}

	public Object clone() {
		return new GiantSudoku(this);
	}

}
