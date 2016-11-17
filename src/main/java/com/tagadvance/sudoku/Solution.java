package com.tagadvance.sudoku;

public interface Solution<V> {

	public Grid<V> getSolution() throws UnsolvableException;

}