package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.model.entity.Actor;
import com.renatmirzoev.moviebookingservice.model.entity.Genre;
import com.renatmirzoev.moviebookingservice.model.entity.Movie;
import com.renatmirzoev.moviebookingservice.utils.JdbcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class MovieRepository {

    private static final String SQL_INSERT_MOVIE = """
        INSERT INTO movies (name, description)
        VALUES (:name, :description)
        RETURNING id;
        """;

    private static final String SQL_INSERT_MOVIE_GENRES = """
        INSERT INTO movie_genres (movie_id, genre_id)
        VALUES (:movieId, :genreId);
        """;

    private static final String SQL_INSERT_MOVIE_ACTORS = """
        INSERT INTO movie_actors (movie_id, actor_id)
        VALUES (:movieId, :actorId);
        """;

    private static final String SQL_SELECT_MOVIE_BY_ID = """
        SELECT
            m.id AS movie_id,
            m.name AS movie_name,
            m.description AS movie_description,
            m.date_created AS movie_date_created,
                
            g.id AS genre_id,
            g.name AS genre_name,
            g.date_created AS genre_date_created,
                
            a.id AS actor_id,
            a.full_name AS actor_full_name,
            a.date_created AS actor_date_created
        FROM movies m
            LEFT JOIN movie_genres mg ON mg.movie_id = m.id
            LEFT JOIN genres g ON mg.genre_id = g.id
            LEFT JOIN movie_actors ma ON ma.movie_id = m.id
            LEFT JOIN actors a ON ma.actor_id = a.id
        WHERE m.id = :id
        """;

    private final JdbcClient jdbcClient;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public long save(Movie movie) {
        return jdbcClient.sql(SQL_INSERT_MOVIE)
            .param("name", movie.getName())
            .param("description", movie.getDescription())
            .query(Long.class)
            .single();
    }

    public int[] addGenresToMovie(long id, Set<Genre> genres) {
        Map<String, Object>[] params = genres.stream()
            .map(genre -> Map.<String, Object>ofEntries(
                Map.entry("movieId", id),
                Map.entry("genreId", genre.getId())
            ))
            .<Map<String, Object>>toArray(Map[]::new);
        return namedParameterJdbcTemplate.batchUpdate(SQL_INSERT_MOVIE_GENRES, params);
    }

    public int[] addActorsToMovie(long id, Set<Actor> actors) {
        Map<String, Object>[] params = actors.stream()
            .map(actor -> Map.<String, Object>ofEntries(
                Map.entry("movieId", id),
                Map.entry("actorId", actor.getId())
            ))
            .<Map<String, Object>>toArray(Map[]::new);
        return namedParameterJdbcTemplate.batchUpdate(SQL_INSERT_MOVIE_ACTORS, params);
    }

    public Optional<Movie> getById(long id) {
        return jdbcClient.sql(SQL_SELECT_MOVIE_BY_ID)
            .param("id", id)
            .query(EXTRACTOR_MOVIE);
    }

    private static final ResultSetExtractor<Optional<Movie>> EXTRACTOR_MOVIE = rs -> {
        Movie movie = null;
        while (rs.next()) {
            if (movie == null) {
                movie = (Movie) new Movie()
                    .setId(rs.getLong("movie_id"))
                    .setName(rs.getString("movie_name"))
                    .setDescription(rs.getString("movie_description"))
                    .setActors(new HashSet<>())
                    .setGenres(new HashSet<>())
                    .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("movie_date_created")));
            }

            long genreId = rs.getLong("genre_id");
            if (!rs.wasNull() && movie != null) {
                Genre genre = (Genre) new Genre()
                    .setId(genreId)
                    .setName(rs.getString("genre_name"))
                    .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("genre_date_created")));
                movie.getGenres().add(genre);
            }

            long actorId = rs.getLong("actor_id");
            if (!rs.wasNull() && movie != null) {
                Actor actor = (Actor) new Actor()
                    .setId(actorId)
                    .setFullName(rs.getString("actor_full_name"))
                    .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("actor_date_created")));
                movie.getActors().add(actor);
            }
        }

        return Optional.ofNullable(movie);
    };
}
