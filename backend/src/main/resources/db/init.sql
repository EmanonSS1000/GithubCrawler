CREATE TABLE IF NOT EXISTS repositories (
    id            BIGSERIAL    PRIMARY KEY,
    full_name     VARCHAR(255) NOT NULL UNIQUE,
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    language      VARCHAR(100),
    stars         INTEGER      NOT NULL DEFAULT 0,
    forks         INTEGER      NOT NULL DEFAULT 0,
    open_issues   INTEGER      NOT NULL DEFAULT 0,
    html_url      VARCHAR(500),
    topics        TEXT[],
    updated_at    TIMESTAMPTZ,
    crawled_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    raw_data      JSONB
);

CREATE INDEX IF NOT EXISTS idx_repositories_language  ON repositories (language);
CREATE INDEX IF NOT EXISTS idx_repositories_stars     ON repositories (stars DESC);
CREATE INDEX IF NOT EXISTS idx_repositories_updated   ON repositories (updated_at DESC);
CREATE INDEX IF NOT EXISTS idx_repositories_raw_data  ON repositories USING GIN (raw_data);
