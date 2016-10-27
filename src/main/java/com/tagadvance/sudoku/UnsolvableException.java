package com.tagadvance.sudoku;

@SuppressWarnings("serial")
class UnsolvableException extends Exception {

	UnsolvableException() {
		super();
	}

	UnsolvableException(String message, Throwable cause) {
		super(message, cause);
	}

	UnsolvableException(String message) {
		super(message);
	}

	UnsolvableException(Throwable cause) {
		super(cause);
	}

}
