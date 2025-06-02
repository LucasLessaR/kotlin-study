-- Migration to create book table

CREATE TABLE IF NOT EXISTS book(
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(5,2) NOT NULL,
    status VARCHAR(255) NOT NULL,
    customer_id INT NOT NULL REFERENCES customer(id)
);