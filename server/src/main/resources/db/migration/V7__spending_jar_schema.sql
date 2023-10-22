CREATE TABLE IF NOT EXISTS spending_jar
(
    id                 BINARY(16) PRIMARY KEY,
    creation_timestamp TIMESTAMP      NOT NULL DEFAULT NOW(),
    name               VARCHAR(100)   NOT NULL,
    amount_to_reach    DECIMAL(19, 4) NOT NULL,
    currency           VARCHAR(3)     NOT NULL,
    percentage         INT            NOT NULL,
    description        TEXT,
    spending_plan_id   BINARY(16)     NOT NULL,
    FOREIGN KEY (spending_plan_id) REFERENCES spending_plan (id) ON DELETE CASCADE ON UPDATE CASCADE
)
;

CREATE UNIQUE INDEX unique_spending_jar_name_plan_id_idx ON spending_jar (spending_plan_id, name);
