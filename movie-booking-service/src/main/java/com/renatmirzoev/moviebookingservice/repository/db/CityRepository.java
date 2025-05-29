package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.utils.JdbcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CityRepository {

    private static final String SQL_INSERT_CITY = """
        INSERT INTO cities (name, country_id)
        VALUES (:name, :countryId)
        RETURNING *;
        """;

    private static final String SQL_SELECT_CITY_BY_ID = """
        SELECT * FROM cities WHERE id = :id;
        """;

    private static final String SQL_COUNT_CITIES_BY_NAME_AND_COUNTRY_ID = """
        SELECT COUNT(*) FROM cities
        WHERE name = :name AND country_id = :countryId;
        """;

    private final JdbcClient jdbcClient;

    public City save(City city) {
        return jdbcClient.sql(SQL_INSERT_CITY)
            .param("name", city.getName())
            .param("countryId", city.getCountryId())
            .query(ROW_MAPPER_CITY)
            .single();
    }

    public Optional<City> getById(long id) {
        return jdbcClient.sql(SQL_SELECT_CITY_BY_ID)
            .param("id", id)
            .query(ROW_MAPPER_CITY)
            .optional();
    }

    public boolean exists(String name, long countryId) {
        int count = jdbcClient.sql(SQL_COUNT_CITIES_BY_NAME_AND_COUNTRY_ID)
            .param("name", name)
            .param("countryId", countryId)
            .query(Integer.class)
            .single();

        return count > 0;
    }

    private static final RowMapper<City> ROW_MAPPER_CITY = (rs, rowNum) -> new City()
        .setId(rs.getLong("id"))
        .setName(rs.getString("name"))
        .setCountryId(rs.getLong("country_id"))
        .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("date_created")));
}
