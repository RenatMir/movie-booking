package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.model.entity.Booking;
import com.renatmirzoev.moviebookingservice.utils.JdbcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookingRepository {

    private static final String SQL_INSERT_BOOKING = """
        INSERT INTO bookings (user_id, showtime_id, seat_id)
        VALUES (:userId, :showtimeId, :seatId)
        RETURNING *;
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT * FROM bookings WHERE id = :id
        """;

    private static final String SQL_COUNT_BOOKINGS_BY_SHOWTIME_ID_AND_SEAT_ID = """
        SELECT COUNT(*) FROM bookings
        WHERE showtime_id = :showtimeId AND seat_id = :seatId
        """;

    private static final String SQL_DELETE_BY_ID = """
        DELETE FROM bookings WHERE id = :id
        RETURNING *;
        """;

    private static final RowMapper<Booking> ROW_MAPPER_BOOKING = (rs, rowNum) -> (Booking) new Booking()
        .setId(rs.getLong("id"))
        .setUserId(rs.getLong("user_id"))
        .setShowtimeId(rs.getLong("showtime_id"))
        .setSeatId(rs.getLong("seat_id"))
        .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("date_created")));

    private final JdbcClient jdbcClient;

    public Optional<Booking> getById(long id) {
        return jdbcClient.sql(SQL_SELECT_BY_ID)
            .param("id", id)
            .query(ROW_MAPPER_BOOKING)
            .optional();
    }


    public Booking save(Booking booking) {
        return jdbcClient.sql(SQL_INSERT_BOOKING)
            .param("userId", booking.getUserId())
            .param("showtimeId", booking.getShowtimeId())
            .param("seatId", booking.getSeatId())
            .query(ROW_MAPPER_BOOKING)
            .single();
    }

    public boolean exists(long showtimeId, long seatId) {
        int count = jdbcClient.sql(SQL_COUNT_BOOKINGS_BY_SHOWTIME_ID_AND_SEAT_ID)
            .param("showtimeId", showtimeId)
            .param("seatId", seatId)
            .query(Integer.class)
            .single();

        return count > 0;
    }

    public Optional<Booking> deleteById(long id) {
        return jdbcClient.sql(SQL_DELETE_BY_ID)
            .param("id", id)
            .query(ROW_MAPPER_BOOKING)
            .optional();
    }
}
