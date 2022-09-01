package com.my.platform.pollservice.service;

import com.my.platform.pollservice.domain.Answer;
import com.my.platform.pollservice.domain.Poll;
import com.my.platform.pollservice.domain.Vote;
import com.my.platform.pollservice.feign.client.UserServiceClient;
import com.my.platform.pollservice.mapper.VoteMapper;
import com.my.platform.pollservice.repository.VoteRepository;
import com.my.platform.pollservice.repository.specification.VoteSpecifications;
import com.my.platform.pollservice.util.security.SecurityUtil;
import com.my.platform.common.enums.EventType;
import com.my.platform.common.event.VoteCreatedEvent;
import com.my.platform.pollservice.api.dto.request.VoteRequestDto;
import com.my.platform.pollservice.api.dto.request.filter.VoteFilterDto;
import com.my.platform.pollservice.api.dto.response.VoteResponseDto;
import com.my.platform.pollservice.api.dto.userservice.UserVoteReductionDto;
import com.my.platform.pollservice.exception.ItemNotFoundException;
import com.my.platform.pollservice.exception.RestException;
import com.my.platform.redis.starter.service.SyncService;
import com.my.platform.pollservice.repository.AnswerRepository;
import com.my.platform.pollservice.repository.PollRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PollRepository pollRepository;
    private final AnswerRepository answerRepository;
    private final VoteMapper voteMapper;
    private final SecurityUtil securityUtil;
    private final SyncService syncService;
    private final UserServiceClient userServiceClient;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(readOnly = true)
    public Page<VoteResponseDto> getVotePage(@NonNull VoteFilterDto filterDto, @NonNull Pageable pageable) {
        log.info("Try to get Page Vote by filter={}, pageSize={}, pageNumber={}",
                filterDto, pageable.getPageSize(), pageable.getPageNumber());
        Specification<Vote> spec = VoteSpecifications.fromVoteFilter(filterDto);
        Page<Vote> votePage = voteRepository.findAll(spec, pageable);
        return votePage.map(voteMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<UUID> getUserVotedPage(@NonNull UUID pollId, @NonNull Pageable pageable) {
        log.info("Try to get Page Vote by pollId={}, pageSize={}, pageNumber={}",
                pollId, pageable.getPageSize(), pageable.getPageNumber());
        return voteRepository.findUsersVoted(pollId, pageable);
    }

    @Transactional
    public void addVote(@NonNull VoteRequestDto requestDto) {
        UUID userId = securityUtil.getCurrentUserId();
        log.info("Try to save vote from dto={}, userId={}", requestDto, userId);
        if (!pollRepository.existsById(requestDto.getPollId())) {
            throw new ItemNotFoundException(Poll.class, requestDto.getPollId());
        }
        if (!answerRepository.existsByIdAndPollId(requestDto.getAnswerId(), requestDto.getPollId())) {
            throw new ItemNotFoundException(Answer.class, requestDto.getAnswerId());
        }
        //TODO get celeb id
        String redisLockKey = String.format("%s-%s-%s", "add-vote", userId.toString(), requestDto.getCelebrityId());
        RLock rLock = syncService.getNamedLock(redisLockKey);
        int amountOfVotes = requestDto.getAmount() == null ? 1 : requestDto.getAmount();
        syncService.doSynchronized(rLock).run(() -> {
            long userVotes = userServiceClient.getUserAvailableVotes(userId, requestDto.getCelebrityId());
            if (userVotes < amountOfVotes) {
                throw new RestException("User keycloakId=" + userId + " has no votes", HttpStatus.CONFLICT);
            }
            Vote vote = voteMapper.toEntity(requestDto, userId);
            voteRepository.save(vote);
            userServiceClient.decrementVoteBalance(
                    UserVoteReductionDto.builder()
                            .keycloakUserId(userId)
                            .celebrityId(requestDto.getCelebrityId())
                            .amount(amountOfVotes)
                            .build()
            );
            publishVoteCreatedEvent(vote, requestDto.getCelebrityId());
        });
    }

    private void publishVoteCreatedEvent(Vote vote, UUID celebrityId) {
        VoteCreatedEvent voteCreatedEvent = voteMapper.toEvent(vote);
        voteCreatedEvent.setCelebrityId(celebrityId);
        voteCreatedEvent.setEventType(EventType.VOTE_CREATED);
        applicationEventPublisher.publishEvent(voteCreatedEvent);
    }
}
