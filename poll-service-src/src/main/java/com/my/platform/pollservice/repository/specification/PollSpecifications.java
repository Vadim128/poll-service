package com.my.platform.pollservice.repository.specification;

import com.my.platform.common.dto.ActivityFilterDto;
import com.my.platform.common.enums.Status;
import com.my.platform.pollservice.domain.Poll;
import com.my.platform.pollservice.api.dto.request.filter.PollFilterDto;
import com.my.platform.pollservice.domain.Poll_;
import com.my.platform.pollservice.exception.RestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.springframework.data.jpa.domain.Specification.where;

public class PollSpecifications {

    public static Specification<Poll> fromPollFilter(PollFilterDto filterDto) {
        return where(titleContainsIgnoreCase(filterDto.getSearchTitle()))
                .and(celebrityId(filterDto.getCelebrityId()))
                .and(statuses(filterDto.getStatuses()));
    }

    public static Specification<Poll> fromActivityFilter(ActivityFilterDto filterDto) {
        return where(titleContainsIgnoreCase(filterDto.getSearchTitle()))
                .and(celebrityId(filterDto.getCelebrityId()))
                .and(statuses(filterDto.getStatuses()));
    }

    public static Specification<Poll> titleContainsIgnoreCase(String searchText) {
        if (StringUtils.isBlank(searchText)) {
            return null;
        }
        String searchTextLower = searchText.toLowerCase();
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get("title")), "%" + searchTextLower + "%");
    }

    public static Specification<Poll> celebrityId(UUID celebrityId) {
        if (celebrityId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("celebrityId"), celebrityId);
    }

    public static Specification<Poll> statuses(Set<Status> statuses) {
        Specification<Poll> spec = (root, query, criteriaBuilder) -> null;
        if (CollectionUtils.isEmpty(statuses)) {
            return spec;
        }
        for (Status status : statuses) {
            spec = spec.or(status(status));
        }
        return spec;
    }

    public static Specification<Poll> status(Status status) {
        switch (status) {
            case ARCHIVED:
                return isArchived(true);
            case DRAFT:
                return isDraft(true)
                        .and(isArchived(false));
            case AWAITING_PUBLICATION:
                return isDraft(false)
                        .and(isArchived(false))
                        .and(isStartTimeAfter(OffsetDateTime.now()));
            case PUBLISHED:
                return isDraft(false)
                        .and(isArchived(false))
                        .and(isStartTimeNullOrBeforeOrEqual(OffsetDateTime.now()))
                        .and(isEndTimeNullOrAfterOrEqual(OffsetDateTime.now()));
            case EXPIRED:
                return isDraft(false)
                        .and(isArchived(false))
                        .and(isEndTimeBefore(OffsetDateTime.now()));
            default:
                throw new RestException("Unknown status=" + status, HttpStatus.BAD_REQUEST);
        }
    }

    public static Specification<Poll> isArchived(boolean archived) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Poll_.archived), archived);
    }

    public static Specification<Poll> isDraft(boolean draft) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Poll_.draft), draft);
    }

    public static Specification<Poll> isStartTimeAfter(OffsetDateTime dateTime) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(Poll_.startTime), dateTime);
    }

    public static Specification<Poll> isStartTimeNullOrBeforeOrEqual(OffsetDateTime dateTime) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.isNull(root.get(Poll_.startTime)),
                        criteriaBuilder.lessThanOrEqualTo(root.get(Poll_.startTime), dateTime)
                );
    }

    public static Specification<Poll> isEndTimeNullOrAfterOrEqual(OffsetDateTime dateTime) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.isNull(root.get(Poll_.endTime)),
                        criteriaBuilder.greaterThanOrEqualTo(root.get(Poll_.endTime), dateTime)
                );
    }

    public static Specification<Poll> isEndTimeBefore(OffsetDateTime dateTime) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.isNotNull(root.get(Poll_.endTime)),
                        criteriaBuilder.lessThan(root.get(Poll_.endTime), dateTime)
                );
    }

    public static Specification<Poll> publishedBefore(OffsetDateTime value, UUID id) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.lessThan(root.get(Poll_.startTime), value),
                        criteriaBuilder.and(
                                criteriaBuilder.equal(root.get(Poll_.startTime), value),
                                criteriaBuilder.lessThan(root.get(Poll_.id), id)
                        )
                );
    }
}
