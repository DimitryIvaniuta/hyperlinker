SET search_path = public;

CREATE TABLE clicks_raw
(
    id          BIGINT PRIMARY KEY DEFAULT nextval('CH_UNIQUE_ID'),
    url_id      BIGINT      NOT NULL REFERENCES urls (id),
    occurred_at TIMESTAMPTZ NOT NULL,
    ip_hash     sha256_hash NOT NULL,
    ua          TEXT,
    referrer    TEXT,
    country     CHAR(2),
    device      VARCHAR(32)
) PARTITION BY RANGE (occurred_at);

COMMENT ON TABLE clicks_raw IS 'High‑volume append‑only log of every redirect.';

CREATE OR REPLACE FUNCTION create_click_partition(p_start DATE)
RETURNS void LANGUAGE plpgsql AS $$
DECLARE
p_end DATE := (p_start + INTERVAL '1 month')::DATE;
    part_name TEXT := format('clicks_raw_%s', to_char(p_start, 'YYYY_MM'));
BEGIN
EXECUTE format(
        'CREATE TABLE IF NOT EXISTS %I PARTITION OF clicks_raw
         FOR VALUES FROM (%L) TO (%L);',
        part_name, p_start, p_end);
END $$;

SELECT create_click_partition(date_trunc('month', current_date));

SELECT create_click_partition(date_trunc('month', current_date) + INTERVAL '1 month');

CREATE INDEX idx_clicks_url_id        ON clicks_raw(url_id);

CREATE INDEX idx_clicks_country_date  ON clicks_raw(country, occurred_at);