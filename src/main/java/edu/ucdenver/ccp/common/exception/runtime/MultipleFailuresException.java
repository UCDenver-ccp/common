package edu.ucdenver.ccp.common.exception.runtime;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")
public class MultipleFailuresException extends RuntimeException {

	private final String message;
	private final List<Throwable> failures;

	public MultipleFailuresException(String message, Throwable... throwables) {
		this.message = message;
		failures = Arrays.asList(throwables);
	}

	public String getMessage() {
		return message;
	}

	public List<Throwable> getFailures() {
		return failures;
	}
	
}
