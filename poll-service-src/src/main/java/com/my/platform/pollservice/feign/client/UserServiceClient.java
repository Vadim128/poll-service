package com.my.platform.pollservice.feign.client;

import com.my.platform.pollservice.api.dto.userservice.UserVoteReductionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = UserServiceClient.NAME,
        url = "${feign." + UserServiceClient.NAME + ".url}",
        path = "${feign." + UserServiceClient.NAME + ".path}"
)
public interface UserServiceClient {
    String NAME = "my-user-service";

    @GetMapping(value = "/api/v1/profile-wallet/vote-balance")
    long getUserAvailableVotes(
            @RequestParam("keycloakUserId") UUID keycloakUserId,
            @RequestParam("celebrityId") UUID celebrityId
    );

    @PostMapping(value = "/api/v1/profile-wallet/vote-balance/decrement")
    void decrementVoteBalance(@RequestBody UserVoteReductionDto dto);

    @GetMapping(value = "/api/v1/user-profiles/is-admin")
    boolean isUserAdmin(
            @RequestParam("keycloakUserId") UUID keycloakUserId,
            @RequestParam("celebrityId") UUID celebrityId
    );

}
