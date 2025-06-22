SET search_path = public;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE DOMAIN sha256_hash AS char(64)
    CHECK ( VALUE ~ '^[0-9a-fA-F]{64}$' );

CREATE TABLE urls
(
    id            BIGINT PRIMARY KEY   DEFAULT nextval('CH_UNIQUE_ID'),
    alias         VARCHAR(16) NOT NULL UNIQUE,
    destination   TEXT        NOT NULL,
    owner_user_id BIGINT      NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires_at    TIMESTAMPTZ,
    deleted_at    TIMESTAMPTZ,
    CONSTRAINT chk_alias_fmt CHECK ( alias ~* '^[A-Za-z0-9_-]{3,16}$')
);

CREATE INDEX idx_urls_owner         ON urls(owner_user_id);

CREATE INDEX idx_urls_expires_at    ON urls(expires_at);

COMMENT ON TABLE  urls IS 'Canonical list of shortened links.';

COMMENT ON COLUMN urls.alias       IS 'Unique human‑friendly or random slug.';

COMMENT ON COLUMN urls.deleted_at  IS 'Soft‑delete timestamp for GDPR workflows.';
