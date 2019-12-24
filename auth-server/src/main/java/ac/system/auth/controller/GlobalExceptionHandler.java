package ac.system.auth.controller;

import ac.system.auth.exception.UserExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(UserExistsException.class)
	public ResponseEntity handleUserExistsException(UserExistsException ex) {
		return new ResponseEntity(ex.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(UnauthorizedUserException.class)
	public ResponseEntity handleUnathorizedException(UnauthorizedUserException ex) {
		return new ResponseEntity(ex.getMessage(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity handleNoSuchElementException(NoSuchElementException ex) {
		return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity handleNoSuchElementException(Exception ex) {
		return new ResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
