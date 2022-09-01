package com.my.platform.pollservice.event.handler;

import com.my.platform.common.event.VoteCreatedEvent;

public interface VoteCreatedEventHandler {

    void handle(VoteCreatedEvent event);
}
