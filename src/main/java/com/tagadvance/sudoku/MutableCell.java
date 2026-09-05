package com.tagadvance.sudoku;

class MutableCell implements Cell {

	private char value;

	MutableCell() {
		super();
	}

	MutableCell(final char value) {
		this.value = value;
	}

	@Override
	public boolean isEmpty() {
		return this.value == '0';
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
