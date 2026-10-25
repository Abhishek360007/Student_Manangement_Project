package com.abhishek.studentms.exception;

public class EmailInUseException extends RuntimeException {

	public EmailInUseException(String message) {
		super(message);
	}

}