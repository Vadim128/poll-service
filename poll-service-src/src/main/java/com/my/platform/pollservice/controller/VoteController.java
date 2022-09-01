package com.my.platform.pollservice.controller;

import com.my.platform.pollservice.api.controller.VoteControllerV1Api;
import com.my.platform.pollservice.api.dto.request.VoteRequestDto;
import com.my.platform.pollservice.api.dto.request.filter.VoteFilterDto;
import com.my.platform.pollservice.api.dto.response.VoteResponseDto;
import com.my.platform.pollservice.service.VoteService;
import com.my.platform.pollservice.util.security.RoleConstants;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@Tag(name = "Vote Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/votes")
public class VoteController implements VoteControllerV1Api {

    private final VoteService voteService;

    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM, RoleConstants.ROLE_TECH_TOKEN})
    public Page<VoteResponseDto> getVotePage(
            @SpringQueryMap VoteFilterDto filterDto,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return voteService.getVotePage(filterDto, pageable);
    }

    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM, RoleConstants.ROLE_TECH_TOKEN})
    public Page<UUID> getUserVotedPage(
            @RequestParam UUID pollId,
            @PageableDefault(sort = {"userId"}, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return voteService.getUserVotedPage(pollId, pageable);
    }

    @Secured({RoleConstants.ROLE_USER})
    public void addVote(
            @Parameter(name = "voteRequestDto", description = "Vote Request Dto")
            @Valid @RequestBody VoteRequestDto requestDto
    ) {
        voteService.addVote(requestDto);
    }
}
