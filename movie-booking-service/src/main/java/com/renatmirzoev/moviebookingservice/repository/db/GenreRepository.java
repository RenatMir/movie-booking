package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.model.entity.Genre;
import com.renatmirzoev.moviebookingservice.utils.JdbcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreRepository {

    private static final String SQL_INSERT_GENRE = """
        INSERT INTO genres (name)
        VALUES (:name)
        RETURNING *;
        """;

    private static final String SQL_SELECT_GENRE_BY_ID = """
        SELECT * FROM genres
        WHERE id = :id;
        """;

    private static final String SQL_COUNT_GENRES_BY_NAME = """
        SELECT COUNT(*) FROM genres WHERE name = :name;
        """;

    private final JdbcClient jdbcClient;

    public Genre save(Genre genre) {
        return jdbcClient.sql(SQL_INSERT_GENRE)
            .param("name", genre.getName())
            .query(ROW_MAPPER_GENRE)
            .single();
    }

    public Optional<Genre> getById(long id) {
        return jdbcClient.sql(SQL_SELECT_GENRE_BY_ID)
            .param("id", id)
            .query(ROW_MAPPER_GENRE)
            .optional();
    }

    public boolean exists(String name) {
        int count = jdbcClient.sql(SQL_COUNT_GENRES_BY_NAME)
            .param("name", name)
            .query(Integer.class)
            .single();

        return count > 0;
    }

    private static final RowMapper<Genre> ROW_MAPPER_GENRE = (rs, rowNum) -> new Genre()
        .setId(rs.getLong("id"))
        .setName(rs.getString("name"))
        .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("date_created")));
}
