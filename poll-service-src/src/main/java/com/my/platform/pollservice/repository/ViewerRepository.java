package com.my.platform.pollservice.repository;

import com.my.platform.pollservice.domain.Viewer;
import com.my.platform.pollservice.repository.result.PollIdWithViewerCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ViewerRepository extends JpaRepository<Viewer, UUID>, JpaSpecificationExecutor<Viewer> {

    @Query(value = ""
            + " select"
            + "     cast(viewer.poll_id as varchar) as pollId,"
            + "     count(CASE WHEN viewer.viewed THEN 1 END) as viewerCounter"
            + " from viewer"
            + " where viewer.poll_id in (:pollIds)"
            + " group by viewer.poll_id",
            nativeQuery = true
    )
    List<PollIdWithViewerCounter> countViewers(List<UUID> pollIds);

    Optional<Viewer> findByPollIdAndUserId(UUID pollId, UUID userId);
}
