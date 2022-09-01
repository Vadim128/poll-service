package com.my.platform.pollservice.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
@Schema(description = "Vote RequestDto DTO")
public class VoteRequestDto {

    @Schema(description = "Poll Id", required = true)
    @NotNull
    private UUID pollId;

    @Schema(description = "Answer Id", required = true)
    @NotNull
    private UUID answerId;

    @Schema(description = "Celebrity Id", required = true)
    @NotNull
    private UUID celebrityId;

    @Schema(description = "Amount of vote")
    @Positive
    private Integer amount;
}
