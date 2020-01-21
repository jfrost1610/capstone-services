package com.eatza.restaurantsearch.exception;

public class RestaurantBadRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RestaurantBadRequestException() {
		super();
	}

	public RestaurantBadRequestException(String msg) {
		super(msg);
	}
}
