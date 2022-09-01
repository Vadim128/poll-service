package com.my.platform.pollservice.api.feign;

import com.my.platform.pollservice.api.controller.PollAdminControllerV1Api;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "poll-admin-client",
        url = "${feign.services.poll.url:localhost:8080}",
        path = "/admin/api/v1/polls",
        primary = false
)
public interface PollAdminApiClient extends PollAdminControllerV1Api {
}
