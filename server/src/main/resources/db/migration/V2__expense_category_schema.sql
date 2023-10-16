CREATE TABLE IF NOT EXISTS expense_category
(
    id                 BINARY(16) PRIMARY KEY,
    account_id         BINARY(16)   NOT NULL,
    name               VARCHAR(100) NOT NULL,
    creation_timestamp TIMESTAMP    NOT NULL DEFAULT NOW()
)
;

CREATE INDEX expense_category_account_id_idx ON expense_category (account_id);
CREATE UNIQUE INDEX unique_category_name_account_id_idx ON expense_category (account_id, name);
