CREATE TABLE bookings
(
    id           SERIAL PRIMARY KEY,
    user_id      INTEGER     NOT NULL REFERENCES users (id),
    showtime_id  INTEGER     NOT NULL REFERENCES showtimes (id),
    seat_id      INTEGER     NOT NULL REFERENCES seats (id),
    date_created timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (seat_id, showtime_id)
);