package com.my.platform.pollservice.repository.result;

import java.util.UUID;

public interface PollIdWithPlayerCounter {

    UUID getPollId();

    Long getPlayerCounter();
}
