package com.tagadvance.sudoku;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicSolution<V> implements Solution<V> {

	AtomicInteger iterations;
	long start;
	long stop;

	private Grid<V> grid;
	private UnsolvableException e;

	public AtomicSolution() {
		super();
		this.iterations = new AtomicInteger();
	}

	public void start() {
		this.start = System.currentTimeMillis();
	}

	public void stop() {
		this.stop = System.currentTimeMillis();
	}

	@Override
	public Grid<V> getSolution() throws UnsolvableException {
		if (grid == null) {
			if (e == null) {
				e = new UnsolvableException();
			}
			throw e;
		}
		return this.grid;
	}

	protected void setSolution(Grid<V> grid) {
		this.grid = grid;
	}

	protected void setException(UnsolvableException e) {
		this.e = e;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("iterations = " + iterations);
		if (start < stop) {
			String lineSeparator = System.getProperty("line.separator");
			sb.append(lineSeparator);
			double seconds = calculateElapsedSeconds();
			sb.append(seconds + " seconds");
		}
		return sb.toString();
	}

	private double calculateElapsedSeconds() {
		return (stop - start) / 1000d;
	}

}
