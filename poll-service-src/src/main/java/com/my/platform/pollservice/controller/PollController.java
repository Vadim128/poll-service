package com.my.platform.pollservice.controller;

import com.my.platform.pollservice.util.security.RoleConstants;
import com.my.platform.pollservice.api.controller.PollControllerV1Api;
import com.my.platform.pollservice.api.dto.request.filter.PollActivitiesFilterDto;
import com.my.platform.pollservice.api.dto.response.PollResponseDto;
import com.my.platform.pollservice.service.PollService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Poll Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/polls")
public class PollController implements PollControllerV1Api {

    private final PollService pollService;

    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public ResponseEntity<PollResponseDto> findPollById(
            @Parameter(name = "id", description = "Poll Id")
            @PathVariable(name = "id") UUID pollId,
            UUID userId
    ) {
        Optional<PollResponseDto> pollResponseDtoO = pollService.findPollById(pollId, userId);
        return ResponseEntity.of(pollResponseDtoO);
    }

    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public List<PollResponseDto> getPollActivities(PollActivitiesFilterDto filterDto) {
        return pollService.getPollActivities(filterDto);
    }
}
