package com.eatza.order.model;

import lombok.Data;

@Data
public class KafkaMessage {
	
	private String message;
	private String sender;

}
