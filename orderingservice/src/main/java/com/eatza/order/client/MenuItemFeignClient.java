package com.eatza.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eatza.order.dto.ItemFetchDto;

@FeignClient(name = "restaurantsearchservice")
@RequestMapping("items")
public interface MenuItemFeignClient {

	@GetMapping(value = "/{itemId}")
	public ItemFetchDto getItemById(@PathVariable("itemId") long itemId);

}
