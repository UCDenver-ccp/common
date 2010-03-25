package edu.ucdenver.ccp.util.exception;

import java.io.IOException;

public class FileProcessingException extends IOException {

	private final int errorLineNumber;

	public FileProcessingException(int errorLineNumber) {
		super();
		this.errorLineNumber = errorLineNumber;
	}

	public FileProcessingException(int errorLineNumber, Throwable cause) {
		super(cause);
		this.errorLineNumber = errorLineNumber;
	}

	public FileProcessingException(String message, int errorLineNumber) {
		super(message);
		this.errorLineNumber = errorLineNumber;
	}

	public FileProcessingException(String message, int errorLineNumber, Throwable cause) {
		super(message, cause);
		this.errorLineNumber = errorLineNumber;
	}

	public int getErrorLineNumber() {
		return errorLineNumber;
	}

}
