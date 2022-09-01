package com.my.platform.pollservice.repository;

import com.my.platform.pollservice.domain.Answer;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {

    boolean existsByIdAndPollId(@NonNull UUID answerId, @NonNull UUID pollId);
}
