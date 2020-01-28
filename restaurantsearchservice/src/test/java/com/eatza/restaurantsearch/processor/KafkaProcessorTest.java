package com.eatza.restaurantsearch.processor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.eatza.restaurantsearch.config.KafkaConfig;
import com.eatza.restaurantsearch.model.KafkaMessage;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = KafkaConfig.class)
public class KafkaProcessorTest {

	@InjectMocks
	private KafkaProcessor processor;

	@Test
	public void testReceiveTopic1() {
		KafkaMessage msg = new KafkaMessage();
		msg.setMessage("Test msg");
		msg.setSender("Test sender");
		processor.receiveTopic1(msg);
	}

}
