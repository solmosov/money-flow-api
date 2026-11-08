ALTER TABLE currencies ADD COLUMN is_popular BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE currencies SET is_popular = TRUE WHERE code IN ('UZS', 'USD', 'EUR', 'RUB', 'KZT');