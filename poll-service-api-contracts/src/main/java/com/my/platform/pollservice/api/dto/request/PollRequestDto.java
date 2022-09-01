package com.my.platform.pollservice.api.dto.request;

import com.my.platform.pollservice.api.dto.AbstractPollDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Poll Request DTO")
public class PollRequestDto extends AbstractPollDto {

    @Schema(description = "Status Draft")
    private boolean draft;

    @Schema(description = "Should contain at least 2 Answers", required = true)
    @NotNull
    @Valid
    private List<AnswerRequestDto> answers;
}
