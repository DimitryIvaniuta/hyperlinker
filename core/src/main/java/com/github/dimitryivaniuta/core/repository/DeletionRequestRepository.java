package com.github.dimitryivaniuta.core.repository;

import com.github.dimitryivaniuta.core.entity.DeletionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Stream;

/**
 * Data‑access layer for GDPR deletion requests.  Streaming read methods let the
 * scheduled purger process large sets without loading everything into memory.
 */
public interface DeletionRequestRepository extends JpaRepository<DeletionRequest, Long> {

    /**
     * Streams all requests whose scheduled purge moment has passed.
     * @param now current timestamp supplied by caller for easy testing
     */
    @Query("SELECT dr FROM DeletionRequest dr WHERE dr.scheduledPurgeAt <= :now")
    Stream<DeletionRequest> streamDueForPurge(@Param("now") Instant now);

    /**
     * Bulk delete after successful purge.
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM DeletionRequest dr WHERE dr.urlId = :urlId")
    void deleteByUrlId(@Param("urlId") Long urlId);
}