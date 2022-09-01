package com.my.platform.pollservice.mapper;

import com.my.platform.common.event.VoteCreatedEvent;
import com.my.platform.pollservice.domain.Vote;
import com.my.platform.pollservice.api.dto.request.VoteRequestDto;
import com.my.platform.pollservice.api.dto.response.VoteResponseDto;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface VoteMapper {

    Vote toEntity(VoteRequestDto requestDto, UUID userId);

    VoteResponseDto toDto(Vote vote);

    VoteCreatedEvent toEvent(Vote vote);
}
