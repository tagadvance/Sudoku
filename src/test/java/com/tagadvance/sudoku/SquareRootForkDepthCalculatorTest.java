package com.tagadvance.sudoku;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.mockito.Mockito;

import com.tagadvance.geometry.Dimension;
import com.tagadvance.geometry.ImmutableDimension;
import com.tagadvance.sudoku.ForkJoinSudokuSolver.SquareRootForkDepthCalculator;

public class SquareRootForkDepthCalculatorTest {

	@Test
	public void calculate9x9GridForkDepthIs3() {
		SquareRootForkDepthCalculator calculator = new SquareRootForkDepthCalculator();
		@SuppressWarnings("unchecked")
		Grid<Void> grid = mock(Grid.class);
		int width = 9, height = 9;
		ImmutableDimension size = new Dimension(width, height);
		Mockito.when(grid.getSize()).thenReturn(size);
		int exptectedForkDepth = 3;
		int forkDepth = calculator.calculateForkDepth(grid);
		assertEquals(exptectedForkDepth, forkDepth);
	}

}
