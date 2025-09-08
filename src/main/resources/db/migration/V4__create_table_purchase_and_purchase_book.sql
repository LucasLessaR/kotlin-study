-- Migration to create purchase table

CREATE TABLE IF NOT EXISTS purchase(
    id SERIAL PRIMARY KEY,
    customer_id int NOT NULL,
    nfe VARCHAR(255) UNIQUE,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

-- Migration to create purchase_book table

CREATE TABLE IF NOT EXISTS purchase_book(
    purchase_id int NOT NULL,
    book_id int NOT NULL,
    FOREIGN KEY (purchase_id) REFERENCES purchase(id),
    FOREIGN KEY (book_id) REFERENCES book(id),
    PRIMARY KEY (purchase_id, book_id)
);