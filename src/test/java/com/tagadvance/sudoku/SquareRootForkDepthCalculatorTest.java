package com.tagadvance.sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tagadvance.geometry.Dimension;
import com.tagadvance.sudoku.ForkJoinSudokuSolver.SquareRootForkDepthCalculator;
import org.junit.jupiter.api.Test;

class SquareRootForkDepthCalculatorTest {

	@Test
	@SuppressWarnings("unchecked")
	void calculate9x9GridForkDepthIs3() {
		final int expectedForkDepth = 3;

		final var calculator = new SquareRootForkDepthCalculator();
		final var grid = (Grid<Void>) mock(Grid.class);
		final int width = 9, height = 9;
		final var size = new Dimension(width, height);
		when(grid.getSize()).thenReturn(size);
		final int forkDepth = calculator.calculateForkDepth(grid);

		assertEquals(expectedForkDepth, forkDepth);
	}

}
