package com.my.platform.pollservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public abstract class AbstractPollDto {

    @Schema(description = "Start Vote Time")
    @Builder.Default
    private OffsetDateTime startTime = OffsetDateTime.now();

    @Schema(description = "End Vote Time")
    private OffsetDateTime endTime;

    @Schema(description = "Title", required = true)
    @Size(max = 255)
    @NotNull
    private String title;

    @Schema(description = "Description", required = true)
    @NotNull
    private String description;

    @Schema(description = "Priority")
    private String priority;

    private Integer announcement;

    private OffsetDateTime announcementTime;

    @Schema(description = "Cover", required = true)
    @Size(max = 1024)
    @NotNull
    private String cover;

    @Schema(description = "Celebrity Id", required = true)
    @NotNull
    private UUID celebrityId;
}
