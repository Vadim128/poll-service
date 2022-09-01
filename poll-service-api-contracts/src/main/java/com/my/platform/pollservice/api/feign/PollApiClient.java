package com.my.platform.pollservice.api.feign;

import com.my.platform.pollservice.api.controller.PollControllerV1Api;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "poll-client",
        url = "${feign.services.poll.url:localhost:8080}",
        path = "/api/v1/polls",
        primary = false
)
public interface PollApiClient extends PollControllerV1Api {
}
