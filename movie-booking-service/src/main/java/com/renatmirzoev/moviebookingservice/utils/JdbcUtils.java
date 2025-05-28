package com.renatmirzoev.moviebookingservice.utils;

import java.sql.Timestamp;
import java.time.Instant;

public class JdbcUtils {

    private JdbcUtils() {}

    public static Instant instantOrNull(Timestamp timestamp) {
        return timestamp != null ? timestamp.toInstant() : null ;
    }
}
