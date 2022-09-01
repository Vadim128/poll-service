package com.my.platform.pollservice.api.dto.request;

import com.my.platform.pollservice.api.dto.AbstractAnswerDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Answer Request DTO")
public class AnswerRequestDto extends AbstractAnswerDto {
    @Schema(description = "Answer Id then update Poll")
    private UUID id;
}
