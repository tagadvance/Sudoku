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
		@SuppressWarnings("unchecked")
		Grid<Void> expectedGrid = mock(Grid.class);
		AtomicSolution<Void> solution = new AtomicSolution<>();
		solution.setSolution(expectedGrid);
		Grid<Void> grid = solution.getSolution();
		Assert.assertNotNull(grid);
	}

	@Test()
	public void testGetSolutionThrowsCustomUnsolvableException() {
		UnsolvableException expectedException = new UnsolvableException("foobar");
		AtomicSolution<Void> solution = new AtomicSolution<>();
		solution.setException(expectedException);
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
