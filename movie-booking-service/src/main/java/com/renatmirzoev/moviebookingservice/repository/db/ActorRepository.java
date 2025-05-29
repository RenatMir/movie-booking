package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.model.entity.Actor;
import com.renatmirzoev.moviebookingservice.utils.JdbcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ActorRepository {

    private static final String SQL_INSERT_ACTOR = """
        INSERT INTO actors (full_name)
        VALUES (:fullName)
        RETURNING *;
        """;

    private static final String SQL_SELECT_ACTOR_BY_ID = """
        SELECT * FROM actors
        WHERE id = :id;
        """;

    private static final String SQL_COUNT_ACTORS_BY_FULL_NAME = """
        SELECT COUNT(*) FROM actors WHERE full_name = :fullName;
        """;

    private static final RowMapper<Actor> ROW_MAPPER_ACTOR = (rs, rowNum) -> (Actor) new Actor()
        .setId(rs.getLong("id"))
        .setFullName(rs.getString("full_name"))
        .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("date_created")));

    private final JdbcClient jdbcClient;

    public Actor save(Actor actor) {
        return jdbcClient.sql(SQL_INSERT_ACTOR)
            .param("fullName", actor.getFullName())
            .query(ROW_MAPPER_ACTOR)
            .single();
    }

    public Optional<Actor> getById(long id) {
        return jdbcClient.sql(SQL_SELECT_ACTOR_BY_ID)
            .param("id", id)
            .query(ROW_MAPPER_ACTOR)
            .optional();
    }

    public boolean exists(String fullName) {
        int count = jdbcClient.sql(SQL_COUNT_ACTORS_BY_FULL_NAME)
            .param("fullName", fullName)
            .query(Integer.class)
            .single();

        return count > 0;
    }
}
