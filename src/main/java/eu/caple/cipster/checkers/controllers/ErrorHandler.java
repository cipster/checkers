package eu.caple.cipster.checkers.controllers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Object> processException(RuntimeException e, WebRequest request) {
		String message = ExceptionUtils.getRootCause(e).getMessage();
		log.debug("400: An internal exception was thrown: ", e);
		return handleException(e, message, BAD_REQUEST, request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> processException(MethodArgumentNotValidException e, WebRequest request) {
		Map<String, String> errors = new HashMap<>();
		e.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		String message = errors.entrySet().stream()
				.map(stringStringEntry -> String.format("%s %s", stringStringEntry.getKey(), stringStringEntry.getValue()))
				.collect(Collectors.joining(", "));
		log.debug("400: A validation exception was thrown: {}", message);
		return handleException(e, message, BAD_REQUEST, request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> processException(Exception e, WebRequest request) {
		String message = ExceptionUtils.getRootCauseMessage(e);
		log.debug("500: An internal exception was thrown: ", e);
		return handleException(e, message, INTERNAL_SERVER_ERROR, request);
	}


	private ResponseEntity<Object> handleException(Exception e, String message, HttpStatus status, WebRequest request) {
		if (HttpStatus.INTERNAL_SERVER_ERROR == status) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, e, SCOPE_REQUEST);
		}

		return new ResponseEntity<>(new ErrorMessage(message), new HttpHeaders(), status);
	}
}
