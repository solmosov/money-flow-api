ALTER TABLE wallets
    ADD COLUMN is_rate_change_confirmed BOOLEAN NOT NULL DEFAULT TRUE;