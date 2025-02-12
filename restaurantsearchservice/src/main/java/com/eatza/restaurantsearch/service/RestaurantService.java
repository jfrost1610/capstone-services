package com.eatza.restaurantsearch.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eatza.restaurantsearch.dto.RestaurantRequestDto;
import com.eatza.restaurantsearch.dto.RestaurantResponseDto;
import com.eatza.restaurantsearch.exception.RestaurantNotFoundException;
import com.eatza.restaurantsearch.model.Menu;
import com.eatza.restaurantsearch.model.MenuItem;
import com.eatza.restaurantsearch.model.Restaurant;
import com.eatza.restaurantsearch.repository.RestaurantRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RestaurantService {

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private MenuService menuService;

	@Autowired
	MenuItemService menuItemService;

	@Cacheable(value = "allrestaurants")
	public RestaurantResponseDto findAllRestaurants(int pageNumber, int pageSize) {

		log.debug("In find all restaurants, creating pageable" + " object for page Number:" + pageNumber
				+ " and page size: " + pageSize);

		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
		log.debug("Calling repository to get all restaurants");
		Page<Restaurant> contentPage = restaurantRepository.findAll(pageable);
		List<Restaurant> restaurantsToReturn = contentPage.getContent();
		return new RestaurantResponseDto(restaurantsToReturn, contentPage.getTotalPages(),
				contentPage.getTotalElements());

	}

	public Restaurant saveRestaurant(RestaurantRequestDto restaurantDto) {
		log.debug("In saveRestaurant, creating object of restaurant to save");
		Restaurant restaurant = new Restaurant(restaurantDto.getName(), restaurantDto.getLocation(),
				restaurantDto.getCuisine(), restaurantDto.getBudget(), restaurantDto.getRating());
		log.debug("calling repository save restaurant method");
		Restaurant savedRestaurant = restaurantRepository.save(restaurant);
		Menu menu = new Menu(restaurantDto.getActiveFrom(), restaurantDto.getActiveTill(), savedRestaurant);
		menuService.saveMenu(menu);
		return savedRestaurant;

	}

	@Cacheable(value = "restaurantbyname")
	public RestaurantResponseDto findByName(String name, int pageNumber, int pageSize) {
		log.debug("In findByName, creating pageable" + " object for page Number:" + pageNumber + " and page size: "
				+ pageSize);
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
		Page<Restaurant> contentPage = restaurantRepository.findByNameContaining(name, pageable);
		List<Restaurant> restaurantsToReturn = contentPage.getContent();
		return new RestaurantResponseDto(restaurantsToReturn, contentPage.getTotalPages(),
				contentPage.getTotalElements());
	}

	@Cacheable(value = "restaurantbylocandcuisine")
	public RestaurantResponseDto findByLocationAndCuisine(String location, String cuisine, int pageNumber,
			int pageSize) {
		log.debug("In findByLocationAndCuisine, creating pageable" + " object for page Number:" + pageNumber
				+ " and page size: " + pageSize);
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
		Page<Restaurant> contentPage = restaurantRepository.findByLocationContainingAndCuisineContaining(location,
				cuisine, pageable);
		if (contentPage.isEmpty()) {
			contentPage = restaurantRepository.findByLocationContainingOrCuisineContaining(location, cuisine, pageable);
		}

		List<Restaurant> restaurantsToReturn = contentPage.getContent();
		return new RestaurantResponseDto(restaurantsToReturn, contentPage.getTotalPages(),
				contentPage.getTotalElements());

	}

	@Cacheable(value = "restaurantbylocationandname")
	public RestaurantResponseDto findByLocationAndName(String location, String name, int pageNumber, int pageSize) {
		log.debug("In findByLocationAndName, creating pageable" + " object for page Number:" + pageNumber
				+ " and page size: " + pageSize);
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
		Page<Restaurant> contentPage = restaurantRepository.findByLocationContainingAndNameContaining(location, name,
				pageable);
		if (contentPage.isEmpty()) {
			contentPage = restaurantRepository.findByLocationContainingOrNameContaining(location, name, pageable);
		}

		List<Restaurant> restaurantsToReturn = contentPage.getContent();
		return new RestaurantResponseDto(restaurantsToReturn, contentPage.getTotalPages(),
				contentPage.getTotalElements());

	}

	@Cacheable(value = "restaurantbybudget")
	public RestaurantResponseDto findByBudget(int budget, int pageNumber, int pageSize) {
		log.debug("In findByBudget, creating pageable" + " object for page Number:" + pageNumber + " and page size: "
				+ pageSize);
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
		Page<Restaurant> contentPage = restaurantRepository.findByBudgetLessThanEqual(budget, pageable);
		List<Restaurant> restaurantsToReturn = contentPage.getContent();
		return new RestaurantResponseDto(restaurantsToReturn, contentPage.getTotalPages(),
				contentPage.getTotalElements());

	}

	@Cacheable(value = "restaurantbyrating")
	public RestaurantResponseDto findByRating(double rating, int pageNumber, int pageSize) {
		log.debug("In findByRating, creating pageable" + " object for page Number:" + pageNumber + " and page size: "
				+ pageSize);
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
		Page<Restaurant> contentPage = restaurantRepository.findByRatingGreaterThanEqual(rating, pageable);
		List<Restaurant> restaurantsToReturn = contentPage.getContent();
		return new RestaurantResponseDto(restaurantsToReturn, contentPage.getTotalPages(),
				contentPage.getTotalElements());

	}

	@Cacheable(value = "restaurantbyid")
	public Restaurant findById(Long id) {
		log.debug("In find by id method, calling repository");
		Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(id);
		if (optionalRestaurant.isPresent()) {
			return optionalRestaurant.get();
		} else {
			log.debug("No restaurant found for given Id");
			throw new RestaurantNotFoundException("No restaurant found for given Id");
		}

	}

	public List<MenuItem> findMenuItemByRestaurantId(Long restaurantId, int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
		Menu menu = menuService.getMenuByRestaurantId(restaurantId);
		Page<MenuItem> menuItemsToReturn = menuItemService.findByMenuId(menu.getId(), pageable);
		if (menuItemsToReturn.hasContent()) {
			return menuItemsToReturn.get().collect(Collectors.toList());
		} else {
			throw new RestaurantNotFoundException("No items found for given restaurant ");
		}
	}
}
