package com.eatza.order.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eatza.order.dto.OrderRequestDto;
import com.eatza.order.dto.OrderUpdateDto;
import com.eatza.order.dto.OrderUpdateResponseDto;
import com.eatza.order.exception.OrderException;
import com.eatza.order.model.KafkaMessage;
import com.eatza.order.model.Order;
import com.eatza.order.publisher.KafkaPublisher;
import com.eatza.order.service.OrderService;

@RestController
public class OrderController {

	@Autowired
	OrderService orderService;

	@Autowired
	KafkaPublisher publisher;

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@PostMapping("/order")
	public ResponseEntity<Order> placeOrder(@RequestBody OrderRequestDto orderRequestDto) throws OrderException {
		logger.debug("In place order method, calling the service");
		Order order = orderService.placeOrder(orderRequestDto);
		logger.debug("Order Placed Successfully");
		return ResponseEntity.status(HttpStatus.OK).body(order);

	}

	@PutMapping("/order/cancel/{orderId}")
	public ResponseEntity<String> cancel(@PathVariable Long orderId) throws OrderException {
		logger.debug("In cancel order method");
		boolean result = orderService.cancelOrder(orderId);
		if (result) {
			logger.debug("Order Cancelled Successfully");
			return ResponseEntity.status(HttpStatus.OK).body("Order Cancelled Successfully");
		} else {
			logger.debug("No records found for respective id");
			throw new OrderException("No records found for respective id");
		}
	}

	@PutMapping("/order")
	public ResponseEntity<OrderUpdateResponseDto> updateOrder(@RequestBody OrderUpdateDto orderUpdateDto)
			throws OrderException {

		logger.debug(" In updateOrder method, calling service");
		OrderUpdateResponseDto updatedResponse = orderService.updateOrder(orderUpdateDto);
		logger.debug("Returning back the object");

		return ResponseEntity.status(HttpStatus.OK).body(updatedResponse);

	}

	@GetMapping("/order/{orderId}")
	public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) throws OrderException {
		logger.debug("In get order by id method, calling service to get Order by ID");
		Optional<Order> order = orderService.getOrderById(orderId);
		if (order.isPresent()) {
			logger.debug("Got order from service");
			return ResponseEntity.status(HttpStatus.OK).body(order.get());
		} else {
			logger.debug("No orders were found");
			throw new OrderException("No result found for specified inputs");
		}
	}

	@GetMapping("/order/value/{orderId}")
	public ResponseEntity<Double> getOrderAmountByOrderId(@PathVariable Long orderId) throws OrderException {
		logger.debug("In get order value by id method, calling service to get Order value");
		double price = orderService.getOrderAmountByOrderId(orderId);

		if (price != 0) {
			logger.debug("returning price: " + price);
			return ResponseEntity.status(HttpStatus.OK).body(price);
		} else {
			logger.debug("No result found for specified inputs");
			throw new OrderException("No result found for specified inputs");
		}
	}

	@PostMapping("/postmessage")
	public ResponseEntity<String> send(@RequestBody KafkaMessage payload) {
		publisher.publishToTopic(payload);
		return ResponseEntity.status(HttpStatus.OK)
				.body("Message: {" + payload.getMessage() + " from " + payload.getSender() + "} sent to topic.");
	}

}
