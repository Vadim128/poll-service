package com.my.platform.pollservice.api.dto.request.filter;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class PollActivitiesFilterDto {

    @NotNull
    private UUID celebrityId;

    @NotNull
    private UUID userId;

    private UUID previousPollId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime previousPollStartTime;

    @Positive
    private int pageSize;
}
