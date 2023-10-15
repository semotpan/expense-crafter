INSERT INTO expense(id, account_id, category_id, payment_type, amount, currency, expense_date,
                    description)
VALUES (UUID_TO_BIN('3b257779-a5db-4e87-9365-72c6f8d4977d'), UUID_TO_BIN('e2709aa2-7907-4f78-98b6-0f36a0c1b5ca'),
        UUID_TO_BIN('3b257779-a5db-4e87-9365-72c6f8d4977d'), 'CASH', '10.0000', 'EUR', '2023-10-13', 'Books buying');
