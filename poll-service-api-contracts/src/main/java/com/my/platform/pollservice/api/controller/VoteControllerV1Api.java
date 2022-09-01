package com.my.platform.pollservice.api.controller;

import com.my.platform.pollservice.api.dto.response.VoteResponseDto;
import com.my.platform.pollservice.api.dto.request.VoteRequestDto;
import com.my.platform.pollservice.api.dto.request.filter.VoteFilterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.UUID;

public interface VoteControllerV1Api {

    @GetMapping
    @Operation(summary = "Get Page of Vote by Page number and Page size")
    @ResponseStatus(HttpStatus.OK)
    Page<VoteResponseDto> getVotePage(
            @SpringQueryMap VoteFilterDto filterDto,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable
    );

    @GetMapping("/users")
    @Operation(summary = "Get Page of User who voted in Poll by Page number and Page size")
    @ResponseStatus(HttpStatus.OK)
    Page<UUID> getUserVotedPage(
            @RequestParam UUID pollId,
            @PageableDefault(sort = {"userId"}, direction = Sort.Direction.ASC) Pageable pageable
    );

    @PostMapping
    @Operation(summary = "Add Vote")
    @ResponseStatus(HttpStatus.CREATED)
    void addVote(
            @Parameter(name = "voteRequestDto", description = "Vote Request Dto")
            @Valid @RequestBody VoteRequestDto requestDto
    );
}
