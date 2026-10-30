package com.abdoheddaya.transactionservice.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransactionEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(TransactionEventPublisher.class);
    private static final String TOPIC = "bank.transactions.created";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TransactionEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(TransactionCreatedEvent event) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            log.error("Could not serialize transaction event {}", event.eventId(), exception);
            return;
        }

        kafkaTemplate.send(TOPIC, event.accountId().toString(), payload)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        log.error("Could not publish transaction event {}", event.eventId(), error);
                    } else {
                        log.info("Published event {} to {} with key {}", event.eventId(), TOPIC, event.accountId());
                    }
                });
    }
}
