package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.model.entity.Row;
import com.renatmirzoev.moviebookingservice.model.entity.Seat;
import com.renatmirzoev.moviebookingservice.utils.JdbcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.TreeSet;

@Repository
@RequiredArgsConstructor
public class RowRepository {

    private static final String SQL_INSERT_ROW = """
        INSERT INTO rows (label, auditorium_id)
        VALUES (:label, :auditoriumId)
        RETURNING id;
        """;

    private static final String SQL_SELECT_ROW_BY_ID = """
        SELECT
        r.id as row_id,
        r.label as row_label,
        r.auditorium_id,
        r.date_created as row_date_created,
        s.id as seat_id,
        s.label as seat_label,
        s.date_created as seat_date_created
        FROM "rows" r
        LEFT JOIN seats s ON s.row_id = r.id
        WHERE r.id = :id
        """;

    private final JdbcClient jdbcClient;

    public long save(Row row) {
        return jdbcClient.sql(SQL_INSERT_ROW)
            .param("label", row.getLabel())
            .param("auditoriumId", row.getAuditoriumId())
            .query(Long.class)
            .single();
    }

    public Optional<Row> getById(long id) {
        return jdbcClient.sql(SQL_SELECT_ROW_BY_ID)
            .param("id", id)
            .query(EXTRACTOR_AUDITORIUM_ROW);
    }

    private static final ResultSetExtractor<Optional<Row>> EXTRACTOR_AUDITORIUM_ROW = rs -> {
        Row row = null;
        while (rs.next()) {
            if (row == null) {
                row = (Row) new Row()
                    .setId(rs.getLong("row_id"))
                    .setLabel(rs.getLong("row_label"))
                    .setAuditoriumId(rs.getLong("auditorium_id"))
                    .setSeats(new TreeSet<>())
                    .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("row_date_created")));
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

        return Optional.ofNullable(row);
    };
}
