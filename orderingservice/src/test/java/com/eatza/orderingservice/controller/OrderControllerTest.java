package com.eatza.orderingservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import com.eatza.order.config.KafkaConfig;
import com.eatza.order.controller.OrderController;
import com.eatza.order.dto.OrderRequestDto;
import com.eatza.order.dto.OrderUpdateDto;
import com.eatza.order.dto.OrderUpdateResponseDto;
import com.eatza.order.dto.OrderedItemsDto;
import com.eatza.order.exception.CustomGlobalExceptionHandler;
import com.eatza.order.model.KafkaMessage;
import com.eatza.order.model.Order;
import com.eatza.order.model.OrderedItem;
import com.eatza.order.publisher.KafkaPublisher;
import com.eatza.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
@ContextConfiguration(classes = { OrderController.class, CustomGlobalExceptionHandler.class, KafkaPublisher.class,
		KafkaConfig.class, KafkaPublisher.class })
public class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderService orderService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private KafkaTemplate<String, KafkaMessage> kafkaTemplate;

	@Test
	public void placeOrder() throws Exception {
		OrderRequestDto orderRequestDto = new OrderRequestDto();
		Order order = new Order(1L, "CREATED", 1L);
		OrderedItemsDto orderedItems = new OrderedItemsDto(1L, 1);
		List<OrderedItemsDto> orderedItemsList = new ArrayList<>();
		orderedItemsList.add(orderedItems);
		orderRequestDto.setCustomerId(1L);
		orderRequestDto.setRestaurantId(1L);
		orderRequestDto.setItems(orderedItemsList);
		when(orderService.placeOrder(any(OrderRequestDto.class))).thenReturn(order);
		RequestBuilder request = MockMvcRequestBuilders.post("/order").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString((orderRequestDto)));
		mockMvc.perform(request).andExpect(status().is(200)).andReturn();
	}

	@Test
	public void cancelOrder() throws Exception {

		when(orderService.cancelOrder(anyLong())).thenReturn(true);
		RequestBuilder request = MockMvcRequestBuilders.put("/order/cancel/1").accept(MediaType.ALL);
		mockMvc.perform(request).andExpect(status().isOk()).andReturn();
	}

	@Test
	public void cancelOrder_failed() throws Exception {

		when(orderService.cancelOrder(anyLong())).thenReturn(false);
		RequestBuilder request = MockMvcRequestBuilders.put("/order/cancel/1").accept(MediaType.ALL);
		mockMvc.perform(request).andExpect(status().is(400)).andReturn();
	}

	@Test
	public void getOrderAmountByOrderId_positive() throws Exception {

		when(orderService.getOrderAmountByOrderId(anyLong())).thenReturn(100.0);
		RequestBuilder request = MockMvcRequestBuilders.get("/order/value/1").accept(MediaType.ALL);
		mockMvc.perform(request).andExpect(status().is(200)).andReturn();

	}

	@Test
	public void getOrderAmountByOrderId_negative() throws Exception {

		when(orderService.getOrderAmountByOrderId(anyLong())).thenReturn(0.0);
		RequestBuilder request = MockMvcRequestBuilders.get("/order/value/1").accept(MediaType.ALL);
		mockMvc.perform(request).andExpect(status().is(400)).andReturn();

	}

	@Test
	public void getOrderById() throws Exception {

		Optional<Order> order = Optional.of(new Order(1L, "CREATED", 1L));
		when(orderService.getOrderById(anyLong())).thenReturn(order);
		RequestBuilder request = MockMvcRequestBuilders.get("/order/1").accept(MediaType.ALL);
		mockMvc.perform(request).andExpect(status().is(200)).andReturn();

	}

	@Test
	public void getOrderById_negative() throws Exception {

		Optional<Order> order = Optional.empty();
		when(orderService.getOrderById(anyLong())).thenReturn(order);
		RequestBuilder request = MockMvcRequestBuilders.get("/order/1").accept(MediaType.ALL);
		mockMvc.perform(request).andExpect(status().is(400)).andReturn();

	}

	@Test
	public void updateOrder() throws Exception {
		List<OrderedItem> orderedList = new ArrayList<>();
		List<OrderedItemsDto> orderedDto = new ArrayList<>();

		OrderUpdateDto orderUpdateDto = new OrderUpdateDto(1L, 1L, orderedDto, 1L);
		when(orderService.updateOrder(any(OrderUpdateDto.class)))
				.thenReturn(new OrderUpdateResponseDto(1L, 1L, "UPDATED", 1L, orderedList));
		RequestBuilder request = MockMvcRequestBuilders.put("/order").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString((orderUpdateDto)));
		mockMvc.perform(request).andExpect(status().is(200)).andReturn();

	}

	@Test
	public void sendMessage() throws Exception {

		KafkaMessage payload = new KafkaMessage();
		payload.setMessage("Test message");
		payload.setSender("Test Sender");
		ListenableFuture<SendResult<String, KafkaMessage>> future = new SettableListenableFuture<>();
		when(kafkaTemplate.send(anyString(), any(KafkaMessage.class))).thenReturn(future);

		RequestBuilder request = MockMvcRequestBuilders.post("/postmessage").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString((payload)));
		mockMvc.perform(request).andExpect(status().is(200)).andReturn();

	}

}
