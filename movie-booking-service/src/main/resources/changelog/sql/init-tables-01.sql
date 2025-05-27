CREATE TABLE customers
(
    id           SERIAL PRIMARY KEY,
    full_name    VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(255),
    date_created timestamptz  NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_players_email ON players (email);