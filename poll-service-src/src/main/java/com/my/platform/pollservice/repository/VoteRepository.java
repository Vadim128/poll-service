package com.my.platform.pollservice.repository;

import com.my.platform.pollservice.domain.Vote;
import com.my.platform.pollservice.repository.result.AnswerWithVoteCounters;
import com.my.platform.pollservice.repository.result.PollIdWithPlayerCounter;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID>, JpaSpecificationExecutor<Vote> {

    @Query("select v.userId from Vote v where v.pollId = :pollId group by v.userId")
    Page<UUID> findUsersVoted(@NonNull @Param("pollId") UUID pollId, Pageable pageable);

    @Query(value = ""
            + "select v.answerId as answerId, sum(v.amount) as voteCounter, "
            + "sum(case v.userId when :userId then v.amount else 0 end) as voteCounterOfCurrentUser "
            + "from Vote v "
            + "where v.pollId in (:pollIds) "
            + "group by v.answerId")
    List<AnswerWithVoteCounters> calculateAnswersVoteCounters(@Param("pollIds") Set<UUID> pollIds, @Param("userId") UUID userId);

    @Query(value = ""
            + " select"
            + "     cast(vote.poll_id as varchar) as pollId,"
            + "     count(distinct vote.user_id) as playerCounter"
            + " from vote"
            + " where vote.poll_id in (:pollIds)"
            + " group by vote.poll_id",
            nativeQuery = true
    )
    List<PollIdWithPlayerCounter> countPlayers(List<UUID> pollIds);
}
