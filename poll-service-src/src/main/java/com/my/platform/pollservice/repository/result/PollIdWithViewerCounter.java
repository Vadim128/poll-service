package com.my.platform.pollservice.repository.result;

import java.util.UUID;

public interface PollIdWithViewerCounter {

    UUID getPollId();

    Long getViewerCounter();
}
