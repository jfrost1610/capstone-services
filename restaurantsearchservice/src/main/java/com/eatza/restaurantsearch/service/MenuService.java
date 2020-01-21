package com.eatza.restaurantsearch.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.eatza.restaurantsearch.model.Menu;
import com.eatza.restaurantsearch.repository.MenuRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MenuService {

	@Autowired
	private MenuRepository menuRepository;

	public Menu saveMenu(Menu menu) {
		log.debug("In save menu method, calling repo");
		return menuRepository.save(menu);
	}

	@Cacheable(value = "menubyid")
	public Optional<Menu> getMenuById(Long id) {
		log.debug("In get menu by ID method, calling repo");
		return menuRepository.findById(id);
	}

	@Cacheable(value = "menusbyrestaurantis")
	public Menu getMenuByRestaurantId(Long id) {
		log.debug("In get menu by restaurant id method, calling repo");
		return menuRepository.findByRestaurant_id(id);
	}
}
