package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import com.renatmirzoev.moviebookingservice.utils.JdbcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TheaterRepository {

    private static final String SQL_INSERT_THEATER = """
        INSERT INTO theaters (name, city_id)
        VALUES (:name, :cityId)
        RETURNING *;
        """;

    private static final String SQL_SELECT_THEATER_BY_ID = """
        SELECT * FROM theaters WHERE id = :id;
        """;

    private static final String SQL_COUNT_CITIES = """
        SELECT COUNT(*) FROM theaters
        WHERE name = :name AND city_id = :cityId;
        """;

    private final JdbcClient jdbcClient;

    public Theater save(Theater theater) {
        return jdbcClient.sql(SQL_INSERT_THEATER)
            .param("name", theater.getName())
            .param("cityId", theater.getCityId())
            .query(ROW_MAPPER_THEATER)
            .single();
    }

    public Optional<Theater> getById(long id) {
        return jdbcClient.sql(SQL_SELECT_THEATER_BY_ID)
            .param("id", id)
            .query(ROW_MAPPER_THEATER)
            .optional();
    }

    public boolean exists(String name, long cityId) {
        int count = jdbcClient.sql(SQL_COUNT_CITIES)
            .param("name", name)
            .param("cityId", cityId)
            .query(Integer.class)
            .single();

        return count > 0;
    }

    private static final RowMapper<Theater> ROW_MAPPER_THEATER = (rs, rowNum) -> new Theater()
        .setId(rs.getLong("id"))
        .setName(rs.getString("name"))
        .setCityId(rs.getLong("city_id"))
        .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("date_created")));
}
