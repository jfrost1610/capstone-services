package com.eatza.order.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaMessage {

	private String message;
	private String sender;

}
