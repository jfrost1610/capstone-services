package com.eatza.restaurantsearch.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(RestaurantNotFoundException.class)
	ResponseEntity<Object> exception(RestaurantNotFoundException exception) {
		log.error("Handling RestaurantNotFoundException! {}", exception.getMessage());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(RestaurantBadRequestException.class)
	ResponseEntity<Object> exception(RestaurantBadRequestException exception) {
		log.error("Handling RestaurantBadRequestException! {}", exception.getMessage());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ItemNotFoundException.class)
	ResponseEntity<Object> exception(ItemNotFoundException exception) {
		log.error("Handling ItemNotFoundException! {}", exception.getMessage());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
	}

}
