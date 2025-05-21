package com.tagadvance.sudoku;

class MutableCell<V> implements Cell<V> {

	private V value;

	MutableCell() {
		super();
	}

	MutableCell(final V value) {
		this.value = value;
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
