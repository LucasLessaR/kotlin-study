-- Migration to create customer table

CREATE TABLE IF NOT EXISTS customer(
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
)