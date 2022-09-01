package com.my.platform.pollservice.api.dto.request.filter;

import lombok.Data;

import java.util.UUID;

@Data
public class VoteFilterDto {
    private UUID userId;
    private UUID pollId;
}
