package com.my.platform.pollservice.repository;

import com.my.platform.pollservice.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID>, JpaSpecificationExecutor<Poll> {

}
