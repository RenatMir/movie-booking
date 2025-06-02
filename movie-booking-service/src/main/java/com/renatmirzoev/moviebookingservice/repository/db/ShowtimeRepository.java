package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.model.entity.Showtime;
import com.renatmirzoev.moviebookingservice.utils.JdbcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ShowtimeRepository {

    private static final String SQL_INSERT_SHOWTIME = """
        INSERT INTO showtimes (movie_id, auditorium_id, date_show)
        VALUES (:movieId, :auditoriumId, :dateShow)
        RETURNING *;
        """;

    private static final String SQL_SELECT_SHOWTIME_BY_ID = """
        SELECT * FROM showtimes WHERE id = :id;
        """;

    private static final String SQL_COUNT_SHOWTIMES_BY_MOVIE_ID_AUDITORIUM_ID_AND_DATE_SHOW = """
        SELECT COUNT(*) FROM showtimes
        WHERE movie_id = :movieId AND auditorium_id = :auditoriumId AND date_show = :dateShow;
        """;

    private static final RowMapper<Showtime> ROW_MAPPER_SHOWTIME = (rs, rowNum) -> (Showtime) new Showtime()
        .setId(rs.getLong("id"))
        .setMovieId(rs.getLong("movie_id"))
        .setAuditoriumId(rs.getLong("auditorium_id"))
        .setDateShow(JdbcUtils.instantOrNull(rs.getTimestamp("date_show")))
        .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("date_created")));

    private final JdbcClient jdbcClient;

    public Showtime save(Showtime showtime) {
        return jdbcClient.sql(SQL_INSERT_SHOWTIME)
            .param("movieId", showtime.getMovieId())
            .param("auditoriumId", showtime.getAuditoriumId())
            .param("dateShow", JdbcUtils.timestampOrNull(showtime.getDateShow()))
            .query(ROW_MAPPER_SHOWTIME)
            .single();
    }

    public Optional<Showtime> getById(long id) {
        return jdbcClient.sql(SQL_SELECT_SHOWTIME_BY_ID)
            .param("id", id)
            .query(ROW_MAPPER_SHOWTIME)
            .optional();
    }

    public boolean exists(long movieId, long auditoriumId, Instant dateShow) {
        int count = jdbcClient.sql(SQL_COUNT_SHOWTIMES_BY_MOVIE_ID_AUDITORIUM_ID_AND_DATE_SHOW)
            .param("movieId", movieId)
            .param("auditoriumId", auditoriumId)
            .param("dateShow", JdbcUtils.timestampOrNull(dateShow))
            .query(Integer.class)
            .single();

        return count > 0;
    }
}
