CREATE TABLE IF NOT EXISTS income
(
    id                 BINARY(16) PRIMARY KEY,
    account_id         BINARY(16)     NOT NULL,
    creation_timestamp TIMESTAMP      NOT NULL DEFAULT NOW(),
    amount             DECIMAL(19, 4) NOT NULL,
    currency           VARCHAR(3)     NOT NULL,
    payment_type       VARCHAR(20)    NOT NULL,
    income_date        DATE           NOT NULL,
    description        TEXT,
    income_source_id   BINARY(16)     NOT NULL,
    FOREIGN KEY (income_source_id) REFERENCES income_source (id)
)
;
