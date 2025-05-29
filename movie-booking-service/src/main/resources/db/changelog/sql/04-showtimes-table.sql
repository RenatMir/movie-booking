CREATE TABLE showtimes
(
    id           SERIAL PRIMARY KEY,
    movie_id     INTEGER     NOT NULL REFERENCES movies (id),
    theater_id   INTEGER     NOT NULL REFERENCES theaters (id),
    date_show    timestamptz NOT NULL,
    date_created timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (movie_id, theater_id, date_show)
);