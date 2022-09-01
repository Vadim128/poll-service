package com.my.platform.pollservice.api.controller;

import com.my.platform.pollservice.api.dto.response.PollResponseDto;
import com.my.platform.common.dto.ActivityFilterDto;
import com.my.platform.common.dto.ActivityPlayersDto;
import com.my.platform.pollservice.api.dto.request.PollRequestDto;
import com.my.platform.pollservice.api.dto.request.filter.PollFilterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface PollAdminControllerV1Api {

    @GetMapping("/{id}")
    @Operation(summary = "Get Poll by Id")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<PollResponseDto> findPollByIdAdmin(
            @Parameter(name = "id", description = "Poll Id")
            @PathVariable(name = "id") UUID pollId
    );

    @GetMapping
    @Operation(summary = "Get Page of Poll by Page number and Page size")
    @ResponseStatus(HttpStatus.OK)
    Page<PollResponseDto> getPollPageAdmin(
            @SpringQueryMap @ParameterObject PollFilterDto pollFilterDto,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) @ParameterObject Pageable pageable
    );

    @GetMapping("/activities")
    @Operation(summary = "Get all Poll Activities")
    @ResponseStatus(HttpStatus.OK)
    Page<PollResponseDto> getPollActivitiesAdmin(
            @Valid @SpringQueryMap @ParameterObject ActivityFilterDto filterDto,
            @PageableDefault(size = Integer.MAX_VALUE) @ParameterObject Pageable pageable,
            @RequestParam boolean paged
    );

    @PostMapping("/players")
    @Operation(summary = "Get Poll Players")
    @ResponseStatus(HttpStatus.OK)
    List<ActivityPlayersDto> findPlayers(@RequestBody List<UUID> pollIds);

    @PostMapping
    @Operation(summary = "Create Poll")
    @ResponseStatus(HttpStatus.CREATED)
    PollResponseDto createPoll(
            @Parameter(name = "pollRequestDto", description = "Poll Request Dto")
            @Valid @RequestBody PollRequestDto pollRequestDto
    );

    @PutMapping("/{id}")
    @Operation(summary = "Update Poll")
    @ResponseStatus(HttpStatus.OK)
    PollResponseDto updatePoll(
            @Parameter(name = "id", description = "Poll Id")
            @PathVariable("id") UUID pollId,
            @Parameter(name = "pollRequestDto", description = "Poll Request Dto")
            @Valid @RequestBody PollRequestDto pollRequestDto
    );

    @PutMapping("/{id}/archive")
    @Operation(summary = "Archive Poll")
    @ResponseStatus(HttpStatus.OK)
    void archivePoll(
            @Parameter(name = "id", description = "Poll Id")
            @PathVariable("id") UUID pollId
    );
}
