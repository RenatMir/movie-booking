package com.renatmirzoev.moviebookingservice.repository;

import com.renatmirzoev.moviebookingservice.model.entity.User;
import com.renatmirzoev.moviebookingservice.utils.JdbcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private static final String SQL_INSERT_USER = """
        INSERT INTO users (full_name, email, phone_number)
        VALUES (:full_name, :email, :phone_number)
        RETURNING *;
        """;

    private static final String SQL_SELECT_USER_BY_ID = """
        SELECT * FROM users WHERE id = :id;
        """;

    private static final String SQL_COUNT_USERS_BY_EMAIL = """
        SELECT COUNT(*) FROM users WHERE email = :email;
        """;

    private final JdbcClient jdbcClient;

    public User save(User user) {
        return jdbcClient.sql(SQL_INSERT_USER)
            .param("full_name", user.getFullName())
            .param("email", user.getEmail())
            .param("phone_number", user.getPhoneNumber())
            .query(ROW_MAPPER_USER)
            .single();
    }

    public Optional<User> getById(long id) {
        return jdbcClient.sql(SQL_SELECT_USER_BY_ID)
            .param("id", id)
            .query(ROW_MAPPER_USER)
            .optional();
    }

    public boolean existsByEmail(String email) {
        int count = jdbcClient.sql(SQL_COUNT_USERS_BY_EMAIL)
            .param("email", email)
            .query(Integer.class)
            .single();

        return count > 0;
    }

    private static final RowMapper<User> ROW_MAPPER_USER = (rs, rowNum) -> new User()
        .setId(rs.getLong("id"))
        .setFullName(rs.getString("full_name"))
        .setEmail(rs.getString("email"))
        .setPhoneNumber(rs.getString("phone_number"))
        .setDateCreated(JdbcUtils.instantOrNull(rs.getTimestamp("date_created")));

}
