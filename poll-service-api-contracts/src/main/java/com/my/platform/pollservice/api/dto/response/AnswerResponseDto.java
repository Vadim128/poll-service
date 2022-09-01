package com.my.platform.pollservice.api.dto.response;

import com.my.platform.pollservice.api.dto.AbstractAnswerDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Answer Response DTO")
public class AnswerResponseDto extends AbstractAnswerDto {
    @Schema(description = "Answer Id", required = true)
    private UUID id;

    @Schema(description = "Vote Counter", required = true)
    @NotNull
    private int voteCounter;

    @Schema(description = "Current User Vote Counter", required = true)
    @NotNull
    private int currentUserVoteCounter;
}
