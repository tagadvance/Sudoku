package com.tagadvance.sudoku;

import static org.mockito.Mockito.mock;

import org.junit.Assert;
import org.junit.Test;

public class AtomicSolutionTest {

	@Test(expected = UnsolvableException.class)
	public void testGetSolutionThrowsUnsolvableException() throws UnsolvableException {
		AtomicSolution<Void> solution = new AtomicSolution<>();
		solution.getSolution();
	}

	@Test()
	public void testGetSolutionReturnsGrid() throws UnsolvableException {
		AtomicSolution<Void> solution = new AtomicSolution<>();
		@SuppressWarnings("unchecked")
		Grid<Void> expectedGrid = mock(Grid.class);
		UnsolvableException exception = null;
		solution.setSolution(expectedGrid, exception);
		Grid<Void> grid = solution.getSolution();
		Assert.assertNotNull(grid);
	}

	@Test()
	public void testGetSolutionThrowsCustomUnsolvableException() {
		AtomicSolution<Void> solution = new AtomicSolution<>();
		Grid<Void> grid = null;
		UnsolvableException expectedException = new UnsolvableException("foobar");
		solution.setSolution(grid, expectedException);
		try {
			solution.getSolution();
		} catch (UnsolvableException e) {
			Assert.assertEquals(expectedException, e);
		}
	}

	@Test()
	public void testGetSolutionUnsolvableExceptionTakesPrecedenceOverGrid() {
		AtomicSolution<Void> solution = new AtomicSolution<>();
		@SuppressWarnings("unchecked")
		Grid<Void> grid = mock(Grid.class);
		UnsolvableException expectedException = new UnsolvableException("foobar");
		solution.setSolution(grid, expectedException);
		try {
			solution.getSolution();
		} catch (UnsolvableException e) {
			Assert.assertEquals(expectedException, e);
		}
	}

	@Test()
	public void testToStringIsNotNull() {
		AtomicSolution<Void> solution = new AtomicSolution<>();
		String s = solution.toString();
		Assert.assertNotNull(s);
	}

}
