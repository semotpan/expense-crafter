CREATE TABLE IF NOT EXISTS income_source
(
    id                 BINARY(16) PRIMARY KEY,
    account_id         BINARY(16)   NOT NULL,
    name               VARCHAR(100) NOT NULL,
    creation_timestamp TIMESTAMP    NOT NULL DEFAULT NOW()
)
;

CREATE INDEX income_source_account_id_idx ON income_source (account_id);
CREATE UNIQUE INDEX unique_income_source_name_account_id_idx ON income_source (account_id, name);
