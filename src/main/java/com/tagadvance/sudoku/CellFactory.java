package com.tagadvance.sudoku;

public interface CellFactory<V> {

	public Cell<V> createCell();

}