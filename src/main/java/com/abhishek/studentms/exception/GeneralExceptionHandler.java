package com.abhishek.studentms.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ StudentNotFoundException.class, ProjectNotFoundException.class })
	public ResponseEntity<ErrorDetails> handleNotFound(RuntimeException ex, WebRequest request) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

	@ExceptionHandler(EmailInUseException.class)
	public ResponseEntity<ErrorDetails> handleEmailInUse(EmailInUseException ex, WebRequest request) {
		return build(HttpStatus.CONFLICT, ex.getMessage(), request);
	}

	@ExceptionHandler(EmptyInputException.class)
	public ResponseEntity<ErrorDetails> handleEmptyInput(EmptyInputException ex, WebRequest request) {
		return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorDetails> handleConstraintViolation(ConstraintViolationException ex,
			WebRequest request) {
		String message = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
				.collect(Collectors.joining(" "));
		return build(HttpStatus.BAD_REQUEST, message, request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> handleAllExceptions(Exception ex, WebRequest request) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(MessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(" "));
		return new ResponseEntity<>(build(HttpStatus.BAD_REQUEST, message, request).getBody(),
				HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		String message = ex.getAllErrors().stream().map(MessageSourceResolvable::getDefaultMessage)
				.collect(Collectors.joining(" "));
		return new ResponseEntity<>(build(HttpStatus.BAD_REQUEST, message, request).getBody(),
				HttpStatus.BAD_REQUEST);
	}

	private ResponseEntity<ErrorDetails> build(HttpStatus status, String message, WebRequest request) {
		String path = request instanceof ServletWebRequest servletRequest
				? servletRequest.getRequest().getRequestURI()
				: request.getDescription(false);
		ErrorDetails body = new ErrorDetails(LocalDateTime.now(), status.value(), message, path);
		return new ResponseEntity<>(body, status);
	}
}
