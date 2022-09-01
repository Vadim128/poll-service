package com.my.platform.pollservice.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
@Schema(description = "Vote Response DTO")
public class VoteResponseDto {
    @Schema(description = "Poll Id", required = true)
    @NotNull
    private UUID pollId;

    @Schema(description = "Answer Id", required = true)
    @NotNull
    private UUID answerId;

    @Schema(description = "Created At", required = true)
    @NotNull
    private OffsetDateTime createdAt;

    @Schema(description = "Amount of vote", required = true)
    @Positive
    @NotNull
    private Integer amount;
}
