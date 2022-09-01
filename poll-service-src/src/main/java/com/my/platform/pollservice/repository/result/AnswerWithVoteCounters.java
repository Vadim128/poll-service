package com.my.platform.pollservice.repository.result;

import java.util.UUID;

public interface AnswerWithVoteCounters {

    UUID getAnswerId();

    int getVoteCounter();

    int getVoteCounterOfCurrentUser();
}
