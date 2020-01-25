package com.eatza.restaurantsearch.model;

import lombok.Data;

@Data
public class KafkaMessage {
	
	private String message;
	private String sender;

}
