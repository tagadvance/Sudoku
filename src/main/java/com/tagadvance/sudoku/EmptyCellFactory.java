package com.tagadvance.sudoku;

public class EmptyCellFactory<V> implements CellFactory<V> {

	public EmptyCellFactory() {
		super();
	}

	@Override
	public Cell<V> createCell() {
		return new EmptyCell<V>();
	}

}
