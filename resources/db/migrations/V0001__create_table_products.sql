CREATE TABLE IF NOT EXISTS products (
    id   UUID PRIMARY KEY,
    name VARCHAR(140) NOT NULL,
    price_in_cents INT8 NOT NULL,
    description varchar(255),
    created_at TIMESTAMP DEFAULT current_timestamp,
    updated_at TIMESTAMP DEFAULT current_timestamp
);
