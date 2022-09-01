package com.my.platform.pollservice.repository.specification;

import com.my.platform.pollservice.domain.Vote;
import com.my.platform.pollservice.api.dto.request.filter.VoteFilterDto;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.springframework.data.jpa.domain.Specification.where;

public class VoteSpecifications {

    public static Specification<Vote> fromVoteFilter(VoteFilterDto filterDto) {
        return where(voteUserId(filterDto.getUserId()))
                .and(votePollId(filterDto.getPollId()));
    }

    public static Specification<Vote> voteUserId(UUID userId) {
        if (userId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("userId"), userId);
    }

    public static Specification<Vote> votePollId(UUID pollId) {
        if (pollId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("pollId"), pollId);
    }
}
