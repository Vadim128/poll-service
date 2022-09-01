package com.my.platform.pollservice.event.handler.impl;

import com.my.platform.common.event.VoteCreatedEvent;
import com.my.platform.pollservice.event.handler.VoteCreatedEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@ConditionalOnProperty(prefix = "spring.kafka.producer", name = "enabled", havingValue = "false", matchIfMissing = true)
@Component
@Slf4j
public class VoteCreatedEventHandlerStub implements VoteCreatedEventHandler {

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(VoteCreatedEvent event) {
        log.info("VoteCreatedEventHandlerStub do nothing");
    }
}
