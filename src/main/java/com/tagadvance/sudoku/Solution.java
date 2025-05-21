package com.tagadvance.sudoku;

public interface Solution<V> {

	Grid<V> getSolution() throws UnsolvableException;

}
