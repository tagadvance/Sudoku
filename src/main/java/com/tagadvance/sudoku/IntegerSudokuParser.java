package com.tagadvance.sudoku;

import com.tagadvance.geometry.ImmutableDimension;
import com.tagadvance.geometry.ImmutablePoint;
import com.tagadvance.geometry.Point;

public class IntegerSudokuParser implements SudokuParser<Integer> {

	@Override
	public void populateSudokuFromString(Grid<Integer> grid, String puzzle) {
		ImmutableDimension size = grid.getSize();

		int x = 0, y = 0;
		for (int i = 0; i < puzzle.length(); i++) {
			ImmutablePoint point = new Point(x, y);
			Cell<Integer> cell = grid.getCellAt(point);
			String value = puzzle.substring(i, i + 1);
			try {
				Integer integer = Integer.parseInt(value);
				cell.setValue(integer);
			} catch (NumberFormatException e) {
				//e.printStackTrace();
			}
			if (++x >= size.getWidth()) {
				y++;
				x = 0;
			}
		}
	}

}
