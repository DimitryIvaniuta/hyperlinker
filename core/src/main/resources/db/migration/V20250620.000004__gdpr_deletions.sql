SET search_path = public;

CREATE TABLE deletion_requests
(
    url_id             BIGINT PRIMARY KEY REFERENCES urls (id) ON DELETE CASCADE,
    requested_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    scheduled_purge_at TIMESTAMPTZ NOT NULL
);

COMMENT ON TABLE deletion_requests IS 'Queue of links awaiting hard deletion.';

ALTER TABLE deletion_requests
    ALTER COLUMN scheduled_purge_at SET DEFAULT (now() + INTERVAL '30 days');

CREATE OR REPLACE FUNCTION purge_click_data(p_url_id BIGINT)
RETURNS void LANGUAGE plpgsql AS $$
DECLARE
part RECORD;
BEGIN
    -- Drop partitions covering rows for this url_id using detach + delete pattern
FOR part IN SELECT inhrelid::regclass AS part_name
            FROM pg_inherits WHERE inhparent = 'clicks_raw'::regclass
    LOOP
        EXECUTE format('DELETE FROM %s WHERE url_id = $1', part.part_name) USING p_url_id;
END LOOP;
DELETE FROM urls WHERE id = p_url_id;
END$$;

CREATE OR REPLACE PROCEDURE run_gdpr_purge()
LANGUAGE plpgsql AS $$
DECLARE r RECORD;
BEGIN
FOR r IN SELECT url_id FROM deletion_requests
         WHERE scheduled_purge_at <= now()
             LOOP
        PERFORM purge_click_data(r.url_id);
DELETE FROM deletion_requests WHERE url_id = r.url_id;
END LOOP;
END$$;
