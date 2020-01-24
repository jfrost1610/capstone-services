package com.eatza.restaurantsearch.processor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaProcessor {

	@KafkaListener(topics = "${kafka.topic}")
	public void receiveTopic1(String payload) {
		log.info("Receiver on topic: " + payload);
	}

}
