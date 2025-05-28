CREATE TABLE countries
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL UNIQUE,
    date_created timestamptz  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cities
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    country_id   INTEGER      NOT NULL REFERENCES countries (id),
    date_created timestamptz  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name, country_id)
);

CREATE TABLE theaters
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    city_id      INTEGER      NOT NULL REFERENCES cities (id),
    date_created timestamptz  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name, city_id)
);