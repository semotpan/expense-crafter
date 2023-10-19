CREATE TABLE IF NOT EXISTS spending_plan
(
    id                 BINARY(16) PRIMARY KEY,
    creation_timestamp TIMESTAMP      NOT NULL DEFAULT NOW(),
    account_id         BINARY(16)     NOT NULL,
    name               VARCHAR(100)   NOT NULL,
    amount             DECIMAL(19, 4) NOT NULL,
    currency           VARCHAR(3)     NOT NULL,
    description        TEXT
)
;

CREATE UNIQUE INDEX unique_spending_plan_name_account_id_idx ON spending_plan (account_id, name);
