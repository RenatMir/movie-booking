package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.model.entity.Seat;
import com.renatmirzoev.moviebookingservice.utils.JdbcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SeatRepository {

    private static final String SQL_INSERT_SEAT = """
        INSERT INTO seats (label, row_id)
        VALUES (:label, :rowId);
        """;

    private static final String SQL_SELECT_SEAT_BY_ID = """
        SELECT * FROM seats
        WHERE id = :id
        """;

    private static final RowMapper<Seat> ROW_MAPPER_SEAT =
        (rs, seatNum) -> (Seat) new Seat()
            .setId(rs.getLong("id"))
            .setLabel(rs.getLong("label"))
            .setRowId(rs.getLong("row_id"))
            .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("date_created")));

    private final JdbcClient jdbcClient;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Set<Integer> save(Set<Seat> seats) {
        SqlParameterSource[] params = seats.stream()
            .map(seat -> new MapSqlParameterSource()
                .addValue("label", seat.getLabel())
                .addValue("rowId", seat.getRowId()))
            .toArray(SqlParameterSource[]::new);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.batchUpdate(SQL_INSERT_SEAT, params, keyHolder);

        return keyHolder.getKeyList()
            .stream()
            .map(keyMap -> (Integer) keyMap.get("id"))
            .collect(Collectors.toSet());
    }

    public Optional<Seat> getById(long id) {
        return jdbcClient.sql(SQL_SELECT_SEAT_BY_ID)
            .param("id", id)
            .query(ROW_MAPPER_SEAT)
            .optional();
    }
}
