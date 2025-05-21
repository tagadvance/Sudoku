package com.tagadvance.sudoku;

public class UnsolvableException extends Exception {

	public UnsolvableException() {
		super();
	}

	public UnsolvableException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsolvableException(String message) {
		super(message);
	}

	public UnsolvableException(Throwable cause) {
		super(cause);
	}

}
