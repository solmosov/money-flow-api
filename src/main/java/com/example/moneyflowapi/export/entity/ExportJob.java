package com.example.moneyflowapi.export.entity;

import com.example.moneyflowapi.common.entity.BaseEntity;
import com.example.moneyflowapi.export.ExportStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "export_jobs")
@Getter
@Setter
@NoArgsConstructor
public class ExportJob extends BaseEntity {
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private ExportStatus status;

    @Column(name = "date_from", nullable = false)
    private Instant dateFrom;

    @Column(name = "date_to", nullable = false)
    private Instant dateTo;

    @Column(name = "zone_id", nullable = false, length = 64)
    private String zoneId;

    @Column(name = "file_name", length = 100)
    private String fileName;

    @Column(name = "download_name", length = 128)
    private String downloadName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
