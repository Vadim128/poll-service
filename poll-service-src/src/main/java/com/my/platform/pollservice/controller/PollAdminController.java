package com.my.platform.pollservice.controller;

import com.my.platform.common.dto.ActivityFilterDto;
import com.my.platform.common.dto.ActivityPlayersDto;
import com.my.platform.pollservice.api.controller.PollAdminControllerV1Api;
import com.my.platform.pollservice.api.dto.request.PollRequestDto;
import com.my.platform.pollservice.api.dto.request.filter.PollFilterDto;
import com.my.platform.pollservice.api.dto.response.PollResponseDto;
import com.my.platform.pollservice.service.PollService;
import com.my.platform.pollservice.util.security.RoleConstants;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Admin Poll Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/v1/polls")
public class PollAdminController implements PollAdminControllerV1Api {

    private final PollService pollService;

    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY})
    public ResponseEntity<PollResponseDto> findPollByIdAdmin(
            @Parameter(name = "id", description = "Poll Id")
            @PathVariable(name = "id") UUID pollId
    ) {
        Optional<PollResponseDto> pollResponseDtoO = pollService.findPollById(pollId);
        return ResponseEntity.of(pollResponseDtoO);
    }

    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY})
    public Page<PollResponseDto> getPollPageAdmin(
            @SpringQueryMap @ParameterObject PollFilterDto pollFilterDto,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) @ParameterObject Pageable pageable
    ) {
        return pollService.getPollPage(pollFilterDto, pageable);
    }

    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public Page<PollResponseDto> getPollActivitiesAdmin(
            @Valid @SpringQueryMap @ParameterObject ActivityFilterDto filterDto,
            @PageableDefault(size = Integer.MAX_VALUE) @ParameterObject Pageable pageable,
            @Parameter boolean paged
    ) {
        if (!paged) {
            pageable = Pageable.unpaged();
        }
        return pollService.getPollPage(filterDto, pageable);
    }

    @Override
    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public List<ActivityPlayersDto> findPlayers(List<UUID> pollIds) {
        return pollService.findPlayers(pollIds);
    }

    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY})
    public PollResponseDto createPoll(
            @Parameter(name = "pollRequestDto", description = "Poll Request Dto")
            @Valid @RequestBody PollRequestDto pollRequestDto
    ) {
        return pollService.createPoll(pollRequestDto);
    }

    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY})
    public PollResponseDto updatePoll(
            @Parameter(name = "id", description = "Poll Id")
            @PathVariable("id") UUID pollId,
            @Parameter(name = "pollRequestDto", description = "Poll Request Dto")
            @Valid @RequestBody PollRequestDto pollRequestDto
    ) {
        return pollService.updatePoll(pollId, pollRequestDto);
    }

    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM})
    public void archivePoll(UUID pollId) {
        pollService.archivePoll(pollId);
    }
}
