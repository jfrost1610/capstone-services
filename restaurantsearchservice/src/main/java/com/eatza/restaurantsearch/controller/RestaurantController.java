package com.eatza.restaurantsearch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eatza.restaurantsearch.dto.RestaurantRequestDto;
import com.eatza.restaurantsearch.dto.RestaurantResponseDto;
import com.eatza.restaurantsearch.exception.ItemNotFoundException;
import com.eatza.restaurantsearch.exception.RestaurantBadRequestException;
import com.eatza.restaurantsearch.exception.RestaurantNotFoundException;
import com.eatza.restaurantsearch.model.MenuItem;
import com.eatza.restaurantsearch.model.Restaurant;
import com.eatza.restaurantsearch.service.MenuItemService;
import com.eatza.restaurantsearch.service.RestaurantService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

	@Autowired
	private RestaurantService restaurantService;

	@Autowired
	MenuItemService menuItemService;

	private static final String RESTAURANT_BAD_REQUEST_MSG = "Page number or Page size cannot be 0 or less";
	private static final String RESTAURANT_NOT_FOUND_MSG = "No Restaurants found for specified inputs";

	@GetMapping
	public ResponseEntity<RestaurantResponseDto> getAllRestaurants(@RequestParam(defaultValue = "1") int pagenumber,
			@RequestParam(defaultValue = "10") int pagesize) {

		log.debug("In getall restaurants method");

		if (pagenumber <= 0 || pagesize <= 0) {
			log.debug("Page number or size cannot be zero or less, throwing exception");
			throw new RestaurantBadRequestException(RESTAURANT_BAD_REQUEST_MSG);
		}

		log.debug("calling service to get restaurants with pagination");
		RestaurantResponseDto responseDto = restaurantService.findAllRestaurants(pagenumber, pagesize);

		if (responseDto.getRestaurants().isEmpty()) {
			log.debug("No restaurants were found");
			throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND_MSG);
		}

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);

	}

	@PostMapping
	public ResponseEntity<String> addRestaurant(@RequestBody RestaurantRequestDto restaurantDto) {

		log.debug("In add restaurants method, calling service");

		restaurantService.saveRestaurant(restaurantDto);
		log.debug("Restaurant saved, returning back");

		return ResponseEntity.status(HttpStatus.OK).body("Restaurant Added successfully");

	}

	@GetMapping("/{name}")
	public ResponseEntity<RestaurantResponseDto> getRestaurantsByName(@PathVariable String name,
			@RequestParam(defaultValue = "1") int pagenumber, @RequestParam(defaultValue = "10") int pagesize) {

		log.debug("In get restaurants by name method");
		if (pagenumber <= 0 || pagesize <= 0) {
			log.debug("Page number or size cannot be zero or less, throwing exception");

			throw new RestaurantBadRequestException(RESTAURANT_BAD_REQUEST_MSG);
		}

		RestaurantResponseDto responseDto = restaurantService.findByName(name, pagenumber, pagesize);
		if (responseDto.getRestaurants().isEmpty()) {
			log.debug("No restaurants were found");

			throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND_MSG);
		}

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}

	@GetMapping("/location/{location}/cuisine/{cuisine}")
	public ResponseEntity<RestaurantResponseDto> getRestaurantsByLocationCuisine(@PathVariable String location,
			@PathVariable String cuisine, @RequestParam(defaultValue = "1") int pagenumber,
			@RequestParam(defaultValue = "10") int pagesize) {

		log.debug("In get restaurants by location and cuisine method");
		if (pagenumber <= 0 || pagesize <= 0) {
			log.debug("Page number or size cannot be zero or less, throwing exception");
			throw new RestaurantBadRequestException(RESTAURANT_BAD_REQUEST_MSG);
		}

		RestaurantResponseDto responseDto = restaurantService.findByLocationAndCuisine(location, cuisine, pagenumber,
				pagesize);
		if (responseDto.getRestaurants().isEmpty()) {
			log.debug("No restaurants were found");
			throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND_MSG);
		}

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);

	}

	@GetMapping("/{name}/location/{location}")
	public ResponseEntity<RestaurantResponseDto> getRestaurantsByLocationName(@PathVariable String location,
			@PathVariable String name, @RequestParam(defaultValue = "1") int pagenumber,
			@RequestParam(defaultValue = "10") int pagesize) {

		log.debug("In get restaurants by location and cuisine method");

		if (pagenumber <= 0 || pagesize <= 0) {
			log.debug("Page number or size cannot be zero or less, throwing exception");
			throw new RestaurantBadRequestException(RESTAURANT_BAD_REQUEST_MSG);
		}

		RestaurantResponseDto responseDto = restaurantService.findByLocationAndName(location, name, pagenumber,
				pagesize);
		if (responseDto.getRestaurants().isEmpty()) {
			log.debug("No restaurants were found");
			throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND_MSG);
		}

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);

	}

	@GetMapping("/budget/{budget}")
	public ResponseEntity<RestaurantResponseDto> getRestaurantsByBudget(@PathVariable int budget,
			@RequestParam(defaultValue = "1") int pagenumber, @RequestParam(defaultValue = "10") int pagesize) {

		log.debug("In get restaurants by budget method");

		if (pagenumber <= 0 || pagesize <= 0) {
			log.debug("Page number or size cannot be zero or less, throwing exception");
			throw new RestaurantBadRequestException(RESTAURANT_BAD_REQUEST_MSG);
		}

		RestaurantResponseDto responseDto = restaurantService.findByBudget(budget, pagenumber, pagesize);
		if (responseDto.getRestaurants().isEmpty()) {
			log.debug("No restaurants were found");
			throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND_MSG);
		}

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);

	}

	@GetMapping("/rating/{rating}")
	public ResponseEntity<RestaurantResponseDto> getRestaurantsByRating(@PathVariable double rating,
			@RequestParam(defaultValue = "1") int pagenumber, @RequestParam(defaultValue = "10") int pagesize) {

		log.debug("In get restaurants by rating method");

		if (pagenumber <= 0 || pagesize <= 0) {
			log.debug("Page number or size cannot be zero or less, throwing exception");
			throw new RestaurantBadRequestException(RESTAURANT_BAD_REQUEST_MSG);
		}

		RestaurantResponseDto responseDto = restaurantService.findByRating(rating, pagenumber, pagesize);
		if (responseDto.getRestaurants().isEmpty()) {
			log.debug("No restaurants were found");
			throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND_MSG);
		}

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);

	}

	@GetMapping("{restaurantid}/items")
	public ResponseEntity<List<MenuItem>> getItemsByRestaurantId(@PathVariable Long restaurantid,
			@RequestParam(defaultValue = "1") int pagenumber, @RequestParam(defaultValue = "10") int pagesize) {

		List<MenuItem> items = restaurantService.findMenuItemByRestaurantId(restaurantid, pagenumber, pagesize);

		if (items.isEmpty()) {
			throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND_MSG);
		}

		return ResponseEntity.status(HttpStatus.OK).body(items);

	}

	@GetMapping("/item/{name}")
	public ResponseEntity<List<Restaurant>> getRestaurantsContainingItem(@PathVariable String name,
			@RequestParam(defaultValue = "1") int pagenumber, @RequestParam(defaultValue = "10") int pagesize)
			throws ItemNotFoundException {
		log.debug("In getRestaurantsContainingItem method, calling service");
		return ResponseEntity.status(HttpStatus.OK).body(menuItemService.findByName(name, pagenumber, pagesize));

	}
}