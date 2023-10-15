CREATE TABLE IF NOT EXISTS expense
(
    id                 BINARY(16) PRIMARY KEY,
    account_id         BINARY(16)     NOT NULL,
    category_id        BINARY(16)     NOT NULL,
    creation_timestamp TIMESTAMP      NOT NULL DEFAULT NOW(),
    payment_type       VARCHAR(20)    NOT NULL,
    amount             DECIMAL(19, 4) NOT NULL,
    currency           VARCHAR(3)     NOT NULL,
    expense_date       DATE           NOT NULL,
    description        TEXT,
    FOREIGN KEY (category_id) REFERENCES expense_category (id)
)
;
