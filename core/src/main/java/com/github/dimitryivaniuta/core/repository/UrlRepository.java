package com.github.dimitryivaniuta.core.repository;

import com.github.dimitryivaniuta.core.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

/**
 * Spring Data repository for {@link Url} entities.
 * <p>
 *   Provides convenience queries frequently needed by the edge and API
 *   micro‑services while inheriting full CRUD & paging from
 *   {@link JpaRepository}.
 * </p>
 */
public interface UrlRepository extends JpaRepository<Url, Long> {

    /**
     * Finds an active (non‑deleted, non‑expired) URL by its alias.
     *
     * @param alias the unique slug (case‑sensitive)
     * @param now   clock‑source used for expiry comparison; typically
     *              {@link java.time.Instant#now()} so the method is easily
     *              testable.
     * @return the matching URL or {@link Optional#empty()} if not found / inactive
     */
    @Query("SELECT u FROM Url u " +
            "WHERE u.alias = :alias " +
            "  AND u.deletedAt IS NULL " +
            "  AND (u.expiresAt IS NULL OR u.expiresAt > :now)")
    Optional<Url> findActiveByAlias(@Param("alias") String alias, @Param("now") Instant now);
}