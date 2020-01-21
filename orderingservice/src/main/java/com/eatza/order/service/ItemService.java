package com.eatza.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eatza.order.client.MenuItemFeignClient;
import com.eatza.order.dto.ItemFetchDto;
import com.eatza.order.model.OrderedItem;
import com.eatza.order.repository.OrderedItemRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemService {

	@Autowired
	OrderedItemRepository itemRepository;

	@Autowired
	MenuItemFeignClient menuItemFeignClient;

	public OrderedItem saveItem(OrderedItem item) {
		return itemRepository.save(item);
	}

	public List<OrderedItem> findbyOrderId(Long id) {
		log.debug("In find item by order id method in service, calling repository to fetch");

		return itemRepository.findbyOrderId(id);
	}

	public void deleteItemsById(Long id) {
		log.debug("In delete item by id method, calling repository");
		itemRepository.deleteById(id);
	}

	@HystrixCommand(fallbackMethod = "findFallbackItemById")
	public ItemFetchDto findItemById(long itemId) {
		return menuItemFeignClient.getItemById(itemId);
	}

	public ItemFetchDto findFallbackItemById(long itemId) {
		return null;
	}

}
