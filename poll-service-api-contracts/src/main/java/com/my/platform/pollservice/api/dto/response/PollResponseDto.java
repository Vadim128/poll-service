package com.my.platform.pollservice.api.dto.response;

import com.my.platform.pollservice.api.dto.AbstractPollDto;
import com.my.platform.common.enums.ActivityType;
import com.my.platform.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Poll Response DTO")
public class PollResponseDto extends AbstractPollDto {

    @Schema(description = "Poll Id", required = true)
    private UUID id;

    @Schema(description = "Status", required = true)
    @NotNull
    private Status status;

    @NotNull
    @Builder.Default
    private ActivityType activityType = ActivityType.POLL;

    @Schema(description = "Should contain at least 2 Answers", required = true)
    @NotNull
    private List<AnswerResponseDto> answers;
}
