package com.eatza.restaurantsearch.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eatza.restaurantsearch.dto.ItemRequestDto;
import com.eatza.restaurantsearch.exception.ItemNotFoundException;
import com.eatza.restaurantsearch.model.MenuItem;
import com.eatza.restaurantsearch.service.menuitemservice.MenuItemService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/items")
public class MenuItemController {

	@Autowired
	MenuItemService menuItemService;

	@PostMapping
	public ResponseEntity<String> addItemsToRestaurantMenu(@RequestBody ItemRequestDto itemRequestDto) {

		log.debug("In addItemsToRestaurantMenu method");
		menuItemService.saveMenuItem(itemRequestDto);
		log.debug("Item added successfully");
		return ResponseEntity.status(HttpStatus.OK).body("Item Added successfully");

	}

	@GetMapping("/{id}")
	public ResponseEntity<MenuItem> getItemById(@PathVariable Long id) throws ItemNotFoundException {
		log.debug("In getItemById method, calling service");
		Optional<MenuItem> item = menuItemService.findById(id);
		if (item.isPresent()) {
			log.debug("got the item");

			return ResponseEntity.status(HttpStatus.OK).body(item.get());
		} else {
			log.debug("Item not found");
			throw new ItemNotFoundException("No Item found for specified inputs");
		}

	}

}
