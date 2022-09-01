package com.my.platform.pollservice.mapper;

import com.my.platform.pollservice.domain.Answer;
import com.my.platform.pollservice.api.dto.request.AnswerRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AnswerMapper {

    Answer toEntity(AnswerRequestDto requestDto, @MappingTarget Answer answer);

}
