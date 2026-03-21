package com.example.ecommerce_order_service.messaging;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.ecommerce_order_service.entities.OutboxEvent;
import com.example.ecommerce_order_service.repositories.OutboxRepository;

import jakarta.transaction.Transactional;

@Service
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(OutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
		super();
		this.outboxRepository = outboxRepository;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Scheduled(fixedDelay = 2000)
    @Transactional
    public void publish() {

        List<OutboxEvent> events = outboxRepository.findByPublishedFalse();

        for (OutboxEvent event : events) {
        										   /* 🔥 KEY */
            kafkaTemplate.send("order-created" ,event.getAggregateId() ,event.getPayload());

            event.setPublished(true);
        }
    }
}
