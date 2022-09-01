package com.my.platform.pollservice.mapper;

import com.my.platform.pollservice.domain.Poll;
import com.my.platform.pollservice.api.dto.request.PollRequestDto;
import com.my.platform.pollservice.api.dto.response.PollResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PollMapper {

    Poll toEntity(PollRequestDto requestDto, @MappingTarget Poll poll);

    @Mapping(target = "answers", ignore = true)
    Poll updateEntityWithoutAnswers(PollRequestDto requestDto, @MappingTarget Poll poll);

    PollResponseDto toDto(Poll poll);

    @Mappings({
            @Mapping(target = "answers", ignore = true)
    })
    PollResponseDto convertBaseActivityFields(Poll poll);

}
