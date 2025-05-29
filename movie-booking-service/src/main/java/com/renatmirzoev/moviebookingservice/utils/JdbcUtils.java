package com.renatmirzoev.moviebookingservice.utils;

import lombok.SneakyThrows;

import java.sql.Array;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class JdbcUtils {

    private JdbcUtils() {
    }

    public static Instant instantOrNull(Timestamp timestamp) {
        return timestamp != null ? timestamp.toInstant() : null;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> Set<T> toSet(Array sqlArray) {
        T[] array = (T[]) sqlArray.getArray();
        return Arrays.stream(array)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
}
