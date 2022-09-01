package com.my.platform.pollservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public abstract class AbstractAnswerDto {

    @Schema(description = "Title", required = true)
    @Size(max = 1024)
    @NotNull
    private String title;

    @Schema(description = "Description")
    @Size(max = 1024)
    private String description;

    @Schema(description = "Media Url")
    @Size(max = 1024)
    private String mediaUrl;

    @Schema(description = "Image Url")
    @Size(max = 1024)
    private String imageUrl;

    @Schema(description = "Number", required = true)
    @NotNull
    private Integer number;
}
