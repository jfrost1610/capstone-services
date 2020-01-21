package com.eatza.order.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import com.eatza.order.dto.ItemFetchDto;
import com.eatza.order.dto.OrderRequestDto;
import com.eatza.order.dto.OrderUpdateDto;
import com.eatza.order.dto.OrderUpdateResponseDto;
import com.eatza.order.dto.OrderedItemsDto;
import com.eatza.order.exception.OrderException;
import com.eatza.order.model.Order;
import com.eatza.order.model.OrderedItem;
import com.eatza.order.repository.OrderRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	ItemService itemService;

	public Order placeOrder(OrderRequestDto orderRequest) throws OrderException {
		log.debug("In place order method, creating order object to persist");
		Order order = new Order(orderRequest.getCustomerId(), "CREATED", orderRequest.getRestaurantId());
		log.debug("saving order in db");
		Order savedOrder = orderRepository.save(order);
		log.debug("Getting all ordered items to persist");
		List<OrderedItemsDto> itemsDtoList = orderRequest.getItems();
		for (OrderedItemsDto itemDto : itemsDtoList) {
			try {
				log.debug("Calling restaurant search service to get item details");
				ItemFetchDto item = itemService.findItemById(itemDto.getItemId());

				if (item == null) {

					orderRepository.delete(order);
					throw new OrderException("Item not found");

				}
				if (!(item.getMenu().getRestaurant().getId().equals(order.getRestaurantId()))) {
					// handle exception here later.
					orderRepository.delete(order);
					throw new OrderException("Item not in given restaurant");
				}
				if (itemDto.getQuantity() <= 0) {
					orderRepository.delete(order);
					throw new OrderException("Quantity of item cannot be 0");
				}
				OrderedItem itemToPersist = new OrderedItem(item.getName(), itemDto.getQuantity(), item.getPrice(),
						savedOrder, item.getId());
				itemService.saveItem(itemToPersist);
			} catch (ResourceAccessException e) {
				throw new OrderException(
						"Something went wrong, looks " + "like restaurant is currently not accepting orders");
			}
		}
		log.debug("Saved order to db");
		return savedOrder;
	}

	public boolean cancelOrder(Long orderId) {
		log.debug("In cancel order service method, calling repository");
		Optional<Order> order = orderRepository.findById(orderId);
		if (order.isPresent()) {
			log.debug("Order was found in db");
			order.get().setStatus("CANCELLED");
			orderRepository.save(order.get());
			return true;
		} else {
			log.debug("Order not found");
			return false;
		}

	}

	public Optional<Order> getOrderById(Long id) {
		return orderRepository.findById(id);
	}

	public double getOrderAmountByOrderId(Long id) {

		Optional<Order> order = orderRepository.findById(id);
		if (order.isPresent()) {
			List<OrderedItem> itemsOrdered = itemService.findbyOrderId(id);
			double total = 0;
			for (OrderedItem item : itemsOrdered) {
				total = total + (item.getPrice() * item.getQuantity());
			}
			return total;
		} else {
			// handle exception here later.
			return 0;
		}

	}

	public OrderUpdateResponseDto updateOrder(OrderUpdateDto orderUpdateRequest) throws OrderException {

		Order order = new Order(orderUpdateRequest.getCustomerId(), "UPDATED", orderUpdateRequest.getRestaurantId());
		Optional<Order> previouslyPersistedOrder = orderRepository.findById(orderUpdateRequest.getOrderId());

		if (!previouslyPersistedOrder.isPresent()) {
			// handle exception properly later
			throw new OrderException("Update Failed, respective order not found");
		}
		if (!(orderUpdateRequest.getRestaurantId().equals(previouslyPersistedOrder.get().getRestaurantId()))) {
			// handle exception properly later
			throw new OrderException("Update Failed, cannot change restaurants while updating order");

		}
		List<OrderedItem> previouslyOrderedItems = itemService.findbyOrderId(previouslyPersistedOrder.get().getId());
		order.setId(previouslyPersistedOrder.get().getId());
		order.setCreateDateTime(previouslyPersistedOrder.get().getCreateDateTime());
		List<OrderedItemsDto> itemsDtoList = orderUpdateRequest.getItems();
		List<OrderedItem> updateItemsListToReturn = new ArrayList<>();
		for (OrderedItemsDto itemDto : itemsDtoList) {
			try {

				ItemFetchDto item = itemService.findItemById(itemDto.getItemId());

				if (item == null) {
					// deleting previously updated items
					for (OrderedItem itemsUpdateToBeReverted : updateItemsListToReturn) {
						itemService.deleteItemsById(itemsUpdateToBeReverted.getId());
					}
					throw new OrderException("Update Failed, item not found in menu");
				}
				if (item.getMenu().getRestaurant().getId() != order.getRestaurantId()) {
					// deleting previously updated items
					for (OrderedItem itemsUpdateToBeReverted : updateItemsListToReturn) {
						itemService.deleteItemsById(itemsUpdateToBeReverted.getId());
					}
					throw new OrderException("Update Failed, item does not belong to respective restaurant");
				}

				if (itemDto.getQuantity() <= 0) {
					// deleting previously updated items
					for (OrderedItem itemsUpdateToBeReverted : updateItemsListToReturn) {
						itemService.deleteItemsById(itemsUpdateToBeReverted.getId());
					}
					throw new OrderException("Update Failed, quantity cannot be zero");
				}

				OrderedItem itemToPersist = new OrderedItem(item.getName(), itemDto.getQuantity(), item.getPrice(),
						previouslyPersistedOrder.get(), item.getId());
				itemToPersist.setId(itemDto.getItemId());
				OrderedItem savedItem = itemService.saveItem(itemToPersist);
				updateItemsListToReturn.add(savedItem);
			} catch (ResourceAccessException e) {
				throw new OrderException(
						"Something went wrong, looks " + "like restaurant is currently not operatable");
			}

		}
		for (OrderedItem previouslyOrderedItem : previouslyOrderedItems) {
			itemService.deleteItemsById(previouslyOrderedItem.getId());
		}
		Order savedOrder = orderRepository.save(order);

		return new OrderUpdateResponseDto(savedOrder.getId(), savedOrder.getCustomerId(), savedOrder.getStatus(),
				savedOrder.getRestaurantId(), updateItemsListToReturn);

	}

}
