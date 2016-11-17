package com.tagadvance.sudoku;

public class EmptyCell<V> implements Cell<V> {

	private V value;

	public EmptyCell() {
		super();
	}

	@Override
	public boolean isEmpty() {
		return this.value == null;
	}

	@Override
	public V getValue() {
		return this.value;
	}

	@Override
	public void setValue(V value) {
		this.value = value;
	}

}
