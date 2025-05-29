CREATE TABLE auditoriums
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    theater_id   INTEGER      NOT NULL REFERENCES theaters (id),
    date_created timestamptz  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name, theater_id)
);

CREATE TABLE auditorium_rows
(
    id            SERIAL PRIMARY KEY,
    label         INTEGER     NOT NULL,
    auditorium_id INTEGER     NOT NULL REFERENCES auditoriums (id),
    date_created  timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (label, auditorium_id)
);

CREATE TABLE auditorium_seats
(
    id           SERIAL PRIMARY KEY,
    label        INTEGER     NOT NULL,
    row_id       INTEGER     NOT NULL REFERENCES auditorium_rows (id),
    date_created timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (label, row_id)
);