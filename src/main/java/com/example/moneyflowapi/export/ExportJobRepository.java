package com.example.moneyflowapi.export;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExportJobRepository extends JpaRepository<ExportJob, UUID> {
    Optional<ExportJob> findByIdAndUserId(UUID id, UUID userId);

    List<ExportJob> findTop200ByStatusInAndExpiresAtBefore(Collection<ExportStatus> statuses, Instant now);

    @Modifying
    @Transactional
    @Query("update ExportJob j set j.status = :status where j.id = :id")
    int updateStatus(@Param("id") UUID id, @Param("status") ExportStatus status);

    @Modifying
    @Transactional
    @Query("""
            update ExportJob j
               set j.status = :status,
                   j.fileName = :fileName,
                   j.fileSize = :fileSize,
                   j.finishedAt = :now,
                   j.expiresAt = :expiresAt
             where j.id = :id
            """)
    int markReady(@Param("id") UUID id,
                  @Param("status") ExportStatus status,
                  @Param("fileName") String fileName,
                  @Param("fileSize") long fileSize,
                  @Param("now") Instant now,
                  @Param("expiresAt") Instant expiresAt);

    @Modifying
    @Transactional
    @Query("""
            update ExportJob j
               set j.status = :status,
                   j.errorMessage = :message,
                   j.finishedAt = :now
             where j.id = :id
            """)
    int markFailed(@Param("id") UUID id,
                   @Param("status") ExportStatus status,
                   @Param("message") String message,
                   @Param("now") Instant now);

    @Modifying
    @Transactional
    @Query("""
            update ExportJob j
               set j.status = :failed,
                   j.errorMessage = 'Server restarted',
                   j.finishedAt = :now
             where j.status in :active
            """)
    int failActive(@Param("failed") ExportStatus failed,
                   @Param("active") Collection<ExportStatus> active,
                   @Param("now") Instant now);
}
