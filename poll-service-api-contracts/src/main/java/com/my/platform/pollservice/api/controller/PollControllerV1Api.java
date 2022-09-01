package com.my.platform.pollservice.api.controller;

import com.my.platform.pollservice.api.dto.response.PollResponseDto;
import com.my.platform.pollservice.api.dto.request.filter.PollActivitiesFilterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface PollControllerV1Api {

    @GetMapping("/{id}")
    @Operation(summary = "Get Poll by Id")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<PollResponseDto> findPollById(
            @Parameter(name = "id", description = "Poll Id")
            @PathVariable(name = "id") UUID pollId,
            @RequestParam UUID userId
    );

    @GetMapping("/activities")
    @Operation(summary = "Get all Poll Activities")
    @ResponseStatus(HttpStatus.OK)
    List<PollResponseDto> getPollActivities(@Valid @SpringQueryMap @ParameterObject PollActivitiesFilterDto filterDto);
}
