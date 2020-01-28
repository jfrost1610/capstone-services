package com.eatza.restaurantsearch.processor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.eatza.restaurantsearch.model.KafkaMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaProcessor {

	@KafkaListener(topics = "${kafka.topic}")
	public void receiveTopic1(KafkaMessage payload) {
		log.info("Message received : [{}]. Sent by {}", payload.getMessage(), payload.getSender());
	}

}
