package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;

public class EmptyCellFactory<V> implements CellFactory<V> {

	private ImmutableSet<V> emptyValues;

	public EmptyCellFactory(ImmutableSet<V> emptyValues) {
		super();
		this.emptyValues = checkNotNull(emptyValues);
	}

	@Override
	public Cell<V> createCell() {
		return new EmptyCell<V>(emptyValues);
	}

}
