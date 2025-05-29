package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.utils.JdbcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CountryRepository {

    private static final String SQL_INSERT_COUNTRY = """
        INSERT INTO countries (name)
        VALUES (:name)
        RETURNING *;
        """;

    private static final String SQL_SELECT_COUNTRY_BY_ID = """
        SELECT * FROM countries WHERE id = :id;
        """;

    private static final String SQL_COUNT_COUNTRIES_BY_NAME = """
        SELECT COUNT(*) FROM countries WHERE name = :name;
        """;

    private static final RowMapper<Country> ROW_MAPPER_COUNTRY = (rs, rowNum) -> (Country) new Country()
        .setId(rs.getLong("id"))
        .setName(rs.getString("name"))
        .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("date_created")));

    private final JdbcClient jdbcClient;

    public Country save(Country country) {
        return jdbcClient.sql(SQL_INSERT_COUNTRY)
            .param("name", country.getName())
            .query(ROW_MAPPER_COUNTRY)
            .single();
    }

    public Optional<Country> getById(long id) {
        return jdbcClient.sql(SQL_SELECT_COUNTRY_BY_ID)
            .param("id", id)
            .query(ROW_MAPPER_COUNTRY)
            .optional();
    }

    public boolean exists(String name) {
        int count = jdbcClient.sql(SQL_COUNT_COUNTRIES_BY_NAME)
            .param("name", name)
            .query(Integer.class)
            .single();

        return count > 0;
    }
}
