CREATE TABLE showtimes
(
    id            SERIAL PRIMARY KEY,
    movie_id      INTEGER     NOT NULL REFERENCES movies (id),
    auditorium_id INTEGER     NOT NULL REFERENCES auditoriums (id),
    date_show     timestamptz NOT NULL,
    date_created  timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (movie_id, auditorium_id, date_show)
);