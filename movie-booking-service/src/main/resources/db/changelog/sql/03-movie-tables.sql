CREATE TABLE genres
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(50) NOT NULL UNIQUE,
    date_created timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE actors
(
    id           SERIAL PRIMARY KEY,
    full_name    VARCHAR(100) NOT NULL UNIQUE,
    date_created timestamptz  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE movies
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    description  TEXT,
    date_created timestamptz  NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_movies_name ON movies (name);

CREATE TABLE movie_genres
(
    movie_id     INTEGER     NOT NULL REFERENCES movies (id),
    genre_id     INTEGER     NOT NULL REFERENCES genres (id),
    date_created timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (movie_id, genre_id)
);

CREATE TABLE movie_actors
(
    movie_id     INTEGER     NOT NULL REFERENCES movies (id),
    actor_id     INTEGER     NOT NULL REFERENCES actors (id),
    date_created timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (movie_id, actor_id)
);
