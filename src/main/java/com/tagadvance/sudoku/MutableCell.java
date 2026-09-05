package com.tagadvance.sudoku;

class MutableCell implements Cell {

	private char value = EMPTY;

	MutableCell() {
		super();
	}

	MutableCell(final char value) {
		this.value = value;
	}

	@Override
	public boolean isEmpty() {
		return this.value == EMPTY;
	}

	@Override
	public char getValue() {
		return this.value;
	}

	@Override
	public void setValue(final char value) {
		this.value = value;
	}

}
