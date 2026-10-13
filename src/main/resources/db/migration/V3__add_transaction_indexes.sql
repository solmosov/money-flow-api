CREATE INDEX IF NOT EXISTS idx_transactions_user_date ON transactions (user_id, date DESC);

CREATE INDEX IF NOT EXISTS idx_transactions_user_category_date ON transactions (user_id, category_id, date DESC);