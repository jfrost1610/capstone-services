package com.eatza.restaurantsearch.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.eatza.restaurantsearch.dto.ItemRequestDto;
import com.eatza.restaurantsearch.model.Menu;
import com.eatza.restaurantsearch.model.MenuItem;
import com.eatza.restaurantsearch.service.MenuItemService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(value = MenuItemController.class)
public class MenuItemControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MenuItemService menuItemService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void addMenuItem() throws Exception {
		ItemRequestDto requestDto = new ItemRequestDto();
		requestDto.setDescription("Dosa");
		requestDto.setMenuId(1L);
		requestDto.setName("Onion Dosa");
		requestDto.setPrice(200);

		when(menuItemService.saveMenuItem(any(ItemRequestDto.class))).thenReturn(new MenuItem());
		RequestBuilder request = MockMvcRequestBuilders.post("/items").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString((requestDto)));
		mockMvc.perform(request).andExpect(status().is(200)).andExpect(content().string("Item Added successfully"))
				.andReturn();
	}

	@Test
	public void getItemById() throws Exception {
		MenuItem menuItem = new MenuItem("Rajma", "Beans", 120, new Menu());
		Optional<MenuItem> returnedItem = Optional.of(menuItem);
		when(menuItemService.findById(anyLong())).thenReturn(returnedItem);
		RequestBuilder request = MockMvcRequestBuilders.get("/items/1?pagenumber=1&pagesize=10").accept(MediaType.ALL);
		mockMvc.perform(request).andExpect(status().is(200))

				.andReturn();
	}

	@Test
	public void getItemById_empty() throws Exception {
		when(menuItemService.findById(anyLong())).thenReturn(Optional.empty());
		RequestBuilder request = MockMvcRequestBuilders.get("/items/1?pagenumber=1&pagesize=10").accept(MediaType.ALL);
		mockMvc.perform(request).andExpect(status().is(404))

				.andReturn();
	}

}
