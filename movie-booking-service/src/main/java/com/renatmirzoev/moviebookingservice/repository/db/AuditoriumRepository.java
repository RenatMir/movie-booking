package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.model.entity.Auditorium;
import com.renatmirzoev.moviebookingservice.model.entity.Row;
import com.renatmirzoev.moviebookingservice.model.entity.Seat;
import com.renatmirzoev.moviebookingservice.utils.JdbcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;

@Repository
@RequiredArgsConstructor
public class AuditoriumRepository {

    private static final String SQL_INSERT_AUDITORIUM = """
        INSERT INTO auditoriums (name, theater_id)
        VALUES (:name, :theaterId)
        RETURNING id;
        """;

    private static final String SQL_SELECT_AUDITORIUM_BY_ID = """
        SELECT
        a.id as auditorium_id,
        a.name as auditorium_name,
        a.theater_id,
        a.date_created as auditorium_date_created,
        r.id as row_id,
        r.label as row_label,
        r.date_created as row_date_created,
        s.id as seat_id,
        s.label as seat_label,
        s.date_created as seat_date_created
        FROM auditoriums a
        LEFT JOIN auditorium_rows r ON r.auditorium_id = a.id
        LEFT JOIN auditorium_seats s ON s.row_id = r.id
        WHERE a.id = :id
        """;

    private static final String SQL_COUNT_AUDITORIUMS_BY_NAME_AND_THEATER_ID = """
        SELECT COUNT(*) FROM auditoriums
        WHERE name = :name AND theater_id = :theaterId;
        """;

    private final JdbcClient jdbcClient;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public long save(Auditorium auditorium) {
        return jdbcClient.sql(SQL_INSERT_AUDITORIUM)
            .param("name", auditorium.getName())
            .param("theaterId", auditorium.getTheaterId())
            .query(Long.class)
            .single();
    }

    public Optional<Auditorium> getById(long id) {
        return jdbcClient.sql(SQL_SELECT_AUDITORIUM_BY_ID)
            .param("id", id)
            .query(EXTRACTOR_AUDITORIUM);
    }

    public boolean exists(String name, long theaterId) {
        int count = jdbcClient.sql(SQL_COUNT_AUDITORIUMS_BY_NAME_AND_THEATER_ID)
            .param("name", name)
            .param("theaterId", theaterId)
            .query(Integer.class)
            .single();

        return count > 0;
    }

    private static final ResultSetExtractor<Optional<Auditorium>> EXTRACTOR_AUDITORIUM = rs -> {
        Auditorium auditorium = null;
        while (rs.next()) {
            if (auditorium == null) {
                auditorium = (Auditorium) new Auditorium()
                    .setId(rs.getLong("auditorium_id"))
                    .setName(rs.getString("auditorium_name"))
                    .setTheaterId(rs.getLong("theater_id"))
                    .setRows(new TreeSet<>(Comparator.comparingLong(Row::getLabel)))
                    .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("auditorium_date_created")));
            }

            long rowId = rs.getLong("row_id");
            Row row = null;
            if (!rs.wasNull() && auditorium != null) {
                row = (Row) new Row()
                    .setId(rowId)
                    .setLabel(rs.getLong("row_label"))
                    .setAuditoriumId(auditorium.getId())
                    .setSeats(new TreeSet<>(Comparator.comparingLong(Seat::getLabel)))
                    .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("row_date_created")));
                auditorium.getRows().add(row);
            }

            long seatId = rs.getLong("seat_id");
            if (!rs.wasNull() && row != null) {
                Seat seat = (Seat) new Seat()
                    .setId(seatId)
                    .setLabel(rs.getLong("seat_label"))
                    .setRowId(row.getId())
                    .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("seat_date_created")));
                row.getSeats().add(seat);
            }
        }

        return Optional.ofNullable(auditorium);
    };
}
