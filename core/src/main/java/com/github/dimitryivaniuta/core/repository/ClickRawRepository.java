package com.github.dimitryivaniuta.core.repository;

import com.github.dimitryivaniuta.core.entity.ClickRaw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

/**
 * Repository for raw click logs.  Mostly write‑heavy, but we expose a handful
 * of aggregate helpers used by the analytics service before it persists
 * roll‑ups.
 */
public interface ClickRawRepository extends JpaRepository<ClickRaw, Long> {

    /**
     * Counts clicks for a given URL within a time range.  Used for back‑fill or
     * ad‑hoc analytics queries.
     */
    @Query("SELECT COUNT(c) FROM ClickRaw c " +
            "WHERE c.urlId = :urlId " +
            "  AND c.occurredAt BETWEEN :from AND :to")
    long countByUrlIdAndRange(@Param("urlId") Long urlId,
                              @Param("from") Instant from,
                              @Param("to")   Instant to);
}