CREATE TABLE currency_conversion_rate (
    currency_id BIGSERIAL PRIMARY KEY,
    currency_code VARCHAR(3) NOT NULL,
    rate_date DATE NOT NULL,
    conversion_rate NUMERIC(10,4) NOT NULL,
    CONSTRAINT uk_currency_date UNIQUE(currency_code, rate_date)
);