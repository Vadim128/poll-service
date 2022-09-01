package com.my.platform.pollservice.event.handler.impl;

import com.my.platform.annotation.OnKafkaProducerEnabled;
import com.my.platform.common.event.VoteCreatedEvent;
import com.my.platform.pollservice.event.handler.VoteCreatedEventHandler;
import com.my.platform.sender.KafkaEventSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
@OnKafkaProducerEnabled
public class VoteCreatedEventHandlerImpl implements VoteCreatedEventHandler {

    private final KafkaEventSender kafkaEventSender;

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(VoteCreatedEvent event) {
        log.info("Try to send event={}, topic={}", event, topic);
        kafkaEventSender.send(event, topic);
    }
}
