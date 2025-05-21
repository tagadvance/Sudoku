package com.tagadvance.sudoku;

import com.google.common.base.MoreObjects;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicSolution<V> implements Solution<V> {

	AtomicInteger iterations;

	private Grid<V> grid;
	private UnsolvableException exception = new UnsolvableException();

	public AtomicSolution() {
		super();
		this.iterations = new AtomicInteger();
	}

	@Override
	public Grid<V> getSolution() throws UnsolvableException {
		if (exception != null) {
			throw exception;
		}

		return grid;
	}

	/**
	 * Only one argument may be <code>null</code>. If both arguments are present then the
	 * {@link UnsolvableException} takes precedence.
	 *
	 * @param grid The solved puzzle grid.
	 * @param e    An {@link UnsolvableException}.
	 */
	protected void setSolution(final Grid<V> grid, final UnsolvableException e) {
		if (grid == null && e == null) {
			throw new IllegalArgumentException("only one argument may be null");
		}

		this.grid = grid;
		this.exception = e;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(AtomicSolution.class).add("grid", grid)
			.add("exception", exception).toString();
	}

}
