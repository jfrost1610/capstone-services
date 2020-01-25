package com.eatza.order.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.eatza.order.model.KafkaMessage;

@Service
public class KafkaPublisher {

	@Value("${kafka.topic}")
	private String topic;

	@Autowired
	private KafkaTemplate<String, KafkaMessage> kafkaTemplate;

	public void publishToTopic(KafkaMessage payload) {
		kafkaTemplate.send(topic, payload);
	}

}
