package com.my.platform.pollservice.api.dto.request.filter;

import com.my.platform.common.enums.Status;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class PollFilterDto {
    private Set<Status> statuses;
    private String searchTitle;
    private UUID celebrityId;
}
