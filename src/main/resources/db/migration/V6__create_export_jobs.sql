CREATE TABLE export_jobs
(
    id        UUID PRIMARY KEY,
    user_id   UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status    VARCHAR(16)  NOT NULL,
    date_from TIMESTAMPTZ NOT NULL,
    date_to   TIMESTAMPTZ NOT NULL,
    file_name VARCHAR(100),
    file_size BIGINT,
    error_message VARCHAR(500),
    finished_at   TIMESTAMPTZ,
    expires_at    TIMESTAMPTZ  NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ
);

CREATE INDEX idx_export_jobs_expires ON export_jobs (status, expires_at);

CREATE UNIQUE INDEX uq_export_jobs_active_user
    ON export_jobs (user_id)
    WHERE status IN ('PENDING', 'RUNNING');