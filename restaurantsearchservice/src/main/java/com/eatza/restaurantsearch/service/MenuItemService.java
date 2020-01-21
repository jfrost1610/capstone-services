package com.eatza.restaurantsearch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eatza.restaurantsearch.dto.ItemRequestDto;
import com.eatza.restaurantsearch.exception.ItemNotFoundException;
import com.eatza.restaurantsearch.exception.MenuNotSavedException;
import com.eatza.restaurantsearch.exception.RestaurantBadRequestException;
import com.eatza.restaurantsearch.model.Menu;
import com.eatza.restaurantsearch.model.MenuItem;
import com.eatza.restaurantsearch.model.Restaurant;
import com.eatza.restaurantsearch.repository.MenuItemRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MenuItemService {

	@Autowired
	private MenuItemRepository menuItemRepository;

	@Autowired
	private MenuService menuService;

	@Autowired
	private RestaurantService restaurantService;

	public MenuItem saveMenuItem(ItemRequestDto itemDto) {
		log.debug("In save menu Item method, calling repo");

		Optional<Menu> menu = menuService.getMenuById(itemDto.getMenuId());
		if (menu.isPresent()) {
			log.debug("Found correponding menu, saving menu item");
			MenuItem menuItem = new MenuItem(itemDto.getName(), itemDto.getDescription(), itemDto.getPrice(),
					menu.get());
			return menuItemRepository.save(menuItem);
		} else {
			log.debug("Crreponding menu not found");
			throw new MenuNotSavedException("Menu not saved, something went wrong");
		}
	}

	@Cacheable(value = "menuitemsbyname")
	public List<Restaurant> findByName(String name, int pagenumber, int pagesize) throws ItemNotFoundException {

		log.debug("In findByName, creating pageable" + " object for page Number:" + pagenumber + " and page size: "
				+ pagesize);

		Pageable pageable = PageRequest.of(pagenumber - 1, pagesize);
		log.debug("Calling repo to find menu items");
		Page<MenuItem> menuItems = menuItemRepository.findByNameContaining(name, pageable);
		if (menuItems.hasContent()) {
			List<Restaurant> restaurantsToReturn = new ArrayList<>();
			for (MenuItem item : menuItems) {

				Optional<Menu> menu = menuService.getMenuById(item.getMenu().getId());
				if (menu.isPresent()) {
					log.debug("Menu is present calling restaurant service to find restaurant by id");
					restaurantsToReturn.add(restaurantService.findById(menu.get().getRestaurant().getId()));
				} else {
					log.debug("No restaurants found, something went wrong");
					throw new RestaurantBadRequestException("No restaurants found, something went wrong");

				}
				if (restaurantsToReturn.size() >= pagesize) {

					break;
				}

			}
			return restaurantsToReturn;
		} else {
			log.debug("Items given are not present in any restaurant");
			throw new ItemNotFoundException("Items given are not present in any restaurant");
		}

	}

	@Cacheable(value = "menuitems")
	public Optional<MenuItem> findById(Long id) {
		return menuItemRepository.findById(id);
	}

	@Cacheable(value = "menuitemsbymenuid")
	public Page<MenuItem> findByMenuId(Long id, Pageable pageable) {
		log.debug("In findByMenuId, calling repository");
		return menuItemRepository.findByMenu_id(id, pageable);
	}

}
