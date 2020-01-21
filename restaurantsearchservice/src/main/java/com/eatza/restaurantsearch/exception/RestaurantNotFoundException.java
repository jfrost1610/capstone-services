package com.eatza.restaurantsearch.exception;

public class RestaurantNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RestaurantNotFoundException() {
		super();
	}

	public RestaurantNotFoundException(String msg) {
		super(msg);
	}

}
