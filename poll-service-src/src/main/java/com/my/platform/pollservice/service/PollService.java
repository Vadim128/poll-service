package com.my.platform.pollservice.service;

import com.my.platform.pollservice.domain.Answer;
import com.my.platform.pollservice.domain.Poll;
import com.my.platform.pollservice.domain.Viewer;
import com.my.platform.pollservice.exception.ItemNotFoundException;
import com.my.platform.pollservice.mapper.AnswerMapper;
import com.my.platform.pollservice.mapper.PollMapper;
import com.my.platform.pollservice.repository.ViewerRepository;
import com.my.platform.pollservice.repository.VoteRepository;
import com.my.platform.pollservice.repository.result.AnswerWithVoteCounters;
import com.my.platform.pollservice.repository.result.PollIdWithPlayerCounter;
import com.my.platform.pollservice.repository.specification.PollSpecifications;
import com.my.platform.pollservice.util.CommonUtils;
import com.my.platform.pollservice.util.security.SecurityUtil;
import com.my.platform.common.dto.ActivityFilterDto;
import com.my.platform.common.dto.ActivityPlayersDto;
import com.my.platform.common.enums.Status;
import com.my.platform.pollservice.api.dto.request.filter.PollActivitiesFilterDto;
import com.my.platform.pollservice.domain.Poll_;
import com.my.platform.pollservice.api.dto.request.AnswerRequestDto;
import com.my.platform.pollservice.api.dto.request.PollRequestDto;
import com.my.platform.pollservice.api.dto.request.filter.PollFilterDto;
import com.my.platform.pollservice.api.dto.response.PollResponseDto;
import com.my.platform.pollservice.repository.PollRepository;
import com.my.platform.pollservice.repository.result.PollIdWithViewerCounter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final ViewerRepository viewerRepository;
    private final VoteRepository voteRepository;
    private final PollMapper pollMapper;
    private final AnswerMapper answerMapper;
    private final SecurityUtil securityUtil;

    @NonNull
    @Transactional
    public Optional<PollResponseDto> findPollById(@NonNull UUID id, UUID userId) {
        log.info("Try to get Poll by ID={}", id);
        return pollRepository.findById(id)
                .map(p -> {
                    saveViewer(p, userId);
                    PollResponseDto dto = pollMapper.toDto(p);
                    calculateAndSetVoteCounters(userId, dto);
                    return dto;
                });
    }

    @NonNull
    @Transactional
    public Optional<PollResponseDto> findPollById(@NonNull UUID id) {
        log.info("Try to get Poll by ID={}", id);
        UUID userId = securityUtil.getCurrentUserId();
        return pollRepository.findById(id)
                .map(p -> {
                    saveViewer(p, userId);
                    PollResponseDto dto = convertAndCalculateStatus(p);
                    calculateAndSetVoteCounters(userId, dto);
                    return dto;
                });
    }

    private void saveViewer(Poll poll, UUID userId) {
        Viewer viewer = viewerRepository.findByPollIdAndUserId(poll.getId(), userId)
                .orElse(Viewer.builder()
                        .poll(poll)
                        .userId(userId)
                        .build()
                );
        viewer.setViewed(true);
        viewerRepository.save(viewer);
    }

    @NonNull
    @Transactional(readOnly = true)
    public Page<PollResponseDto> getPollPage(@NonNull PollFilterDto filterDto, @NonNull Pageable pageable) {
        log.info("Try to get Page Poll by filter={}, pageSize={}, pageNumber={}",
                filterDto, pageable.getPageSize(), pageable.getPageNumber());
        Specification<Poll> spec = PollSpecifications.fromPollFilter(filterDto);
        Page<Poll> pollPage = pollRepository.findAll(spec, pageable);
        Page<PollResponseDto> dtoPage = pollPage.map(this::convertAndCalculateStatus);
        calculateAndSetVoteCounters(securityUtil.getCurrentUserId(), dtoPage.getContent().toArray(PollResponseDto[]::new));
        return dtoPage;
    }

    private void calculateAndSetVoteCounters(UUID userId, PollResponseDto... polls) {
        Set<UUID> pollIds = Arrays.stream(polls)
                .map(PollResponseDto::getId)
                .collect(Collectors.toSet());
        Map<UUID, AnswerWithVoteCounters> answerVoteCountersByAnswerId = voteRepository
                .calculateAnswersVoteCounters(pollIds, userId).stream()
                .collect(Collectors.toMap(AnswerWithVoteCounters::getAnswerId, a -> a));
        for (PollResponseDto pollResponseDto : polls) {
            pollResponseDto.getAnswers().forEach(answer -> {
                AnswerWithVoteCounters answerWithVoteCounters = answerVoteCountersByAnswerId.get(answer.getId());
                if (answerWithVoteCounters != null) {
                    answer.setVoteCounter(answerWithVoteCounters.getVoteCounter());
                    answer.setCurrentUserVoteCounter(answerWithVoteCounters.getVoteCounterOfCurrentUser());
                }
            });
        }
    }

    @NonNull
    @Transactional(readOnly = true)
    public Page<PollResponseDto> getPollPage(@NonNull ActivityFilterDto filterDto, Pageable pageable) {
        log.info("Try to get Poll Activities by filter={}", filterDto);
        Specification<Poll> spec = PollSpecifications.fromActivityFilter(filterDto);
        Page<Poll> polls = pollRepository.findAll(spec, pageable);
        return polls.map(this::convertAndCalculateStatus);
    }

    @NonNull
    @Transactional(readOnly = true)
    public List<PollResponseDto> getPollActivities(PollActivitiesFilterDto filterDto) {
        log.info("Try to get Poll Activities by filter={}", filterDto);
        Specification<Poll> spec = PollSpecifications.celebrityId(filterDto.getCelebrityId())
                .and(PollSpecifications.status(Status.PUBLISHED));
        if (filterDto.getPreviousPollId() != null && filterDto.getPreviousPollStartTime() != null) {
            spec = spec.and(
                    PollSpecifications.publishedBefore(
                            filterDto.getPreviousPollStartTime(),
                            filterDto.getPreviousPollId()
                    )
            );
        }
        Sort sort = Sort.by(Poll_.START_TIME).descending().and(Sort.by(Poll_.ID).descending());
        PageRequest pageRequest = PageRequest.of(0, filterDto.getPageSize(), sort);
        Page<Poll> polls = pollRepository.findAll(spec, pageRequest);
        return polls.getContent().stream()
                .map(pollMapper::convertBaseActivityFields)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityPlayersDto> findPlayers(List<UUID> pollIds) {
        Map<UUID, Long> viewerCountersByPollId;
        Map<UUID, Long> playerCountersByPollId;
        if (!pollIds.isEmpty()) {
            viewerCountersByPollId = viewerRepository.countViewers(pollIds).stream()
                    .collect(Collectors.toMap(PollIdWithViewerCounter::getPollId, PollIdWithViewerCounter::getViewerCounter));
            playerCountersByPollId = voteRepository.countPlayers(pollIds).stream()
                    .collect(Collectors.toMap(PollIdWithPlayerCounter::getPollId, PollIdWithPlayerCounter::getPlayerCounter));
        } else {
            viewerCountersByPollId = new HashMap<>();
            playerCountersByPollId = new HashMap<>();
        }
        return pollIds.stream()
                .map(pollId ->
                        ActivityPlayersDto.builder()
                                .activityId(pollId)
                                .views(CommonUtils.toPrimitive(viewerCountersByPollId.get(pollId)))
                                .players(CommonUtils.toPrimitive(playerCountersByPollId.get(pollId)))
                                .build()
                )
                .collect(Collectors.toList());
    }

    @NonNull
    @Transactional
    public PollResponseDto createPoll(@NonNull PollRequestDto requestDto) {
        log.info("Try to create Poll from dto={}", requestDto);
        Poll poll = pollMapper.toEntity(requestDto, new Poll());
        pollRepository.save(poll);
        return convertAndCalculateStatus(poll);
    }

    @NonNull
    @Transactional
    public PollResponseDto updatePoll(@NonNull UUID pollId, @NonNull PollRequestDto requestDto) {
        log.info("Try to update Poll id={} from dto={}", pollId, requestDto);
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ItemNotFoundException(Poll.class, pollId));
        poll = pollMapper.updateEntityWithoutAnswers(requestDto, poll);
        updatePollAnswers(poll, requestDto.getAnswers());
        pollRepository.save(poll);
        return convertAndCalculateStatus(poll);
    }

    private void updatePollAnswers(Poll poll, List<AnswerRequestDto> answerDtos) {
        List<Answer> answersForRemove = answersForRemove(poll.getAnswers(), answerDtos);
        poll.getAnswers().removeAll(answersForRemove);
        Map<UUID, Answer> answerById = poll.getAnswers().stream()
                .collect(Collectors.toMap(Answer::getId, a -> a));
        answerDtos.forEach(answerDto -> {
            Answer answer;
            if (answerDto.getId() != null) {
                answer = answerById.get(answerDto.getId());
            } else {
                answer = new Answer();
                poll.addAnswer(answer);
            }
            answerMapper.toEntity(answerDto, answer);
        });
    }

    private List<Answer> answersForRemove(List<Answer> answers, List<AnswerRequestDto> answerDtos) {
        Set<UUID> answerRequestIds = answerDtos.stream()
                .filter(Objects::nonNull)
                .map(AnswerRequestDto::getId)
                .collect(Collectors.toSet());
        return answers.stream()
                .filter(a -> !answerRequestIds.contains(a.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void archivePoll(@NonNull UUID pollId) {
        log.info("Try to archive Poll id={}", pollId);
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ItemNotFoundException(Poll.class, pollId));
        poll.setArchived(true);
        pollRepository.save(poll);
    }

    private PollResponseDto convertAndCalculateStatus(Poll poll) {
        PollResponseDto dto = pollMapper.toDto(poll);
        dto.setStatus(calculateStatus(poll));
        return dto;
    }

    private Status calculateStatus(Poll poll) {
        if (poll.isArchived()) {
            return Status.ARCHIVED;
        }
        if (poll.isDraft()) {
            return Status.DRAFT;
        }
        if (poll.getStartTime() != null && poll.getStartTime().isAfter(OffsetDateTime.now())) {
            return Status.AWAITING_PUBLICATION;
        }
        if (poll.getEndTime() != null && poll.getEndTime().isBefore(OffsetDateTime.now())) {
            return Status.EXPIRED;
        }
        return Status.PUBLISHED;
    }
}
