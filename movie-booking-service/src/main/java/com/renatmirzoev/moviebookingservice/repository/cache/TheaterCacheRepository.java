package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import com.renatmirzoev.moviebookingservice.utils.JsonUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class TheaterCacheRepository extends AbstractCacheRepository {

    private static final String KEY_THEATER = "theater:";
    private static final String KEY_THEATER_EXISTS = "theaterExists:";

    private static final Duration TTL_THEATER = Duration.ofDays(30);
    private static final Duration TTL_THEATER_EXISTS = Duration.ofDays(30);

    public TheaterCacheRepository(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    static String keyTheater(long theaterId) {
        return KEY_THEATER + theaterId;
    }

    static String keyTheaterExists(String name, long cityId) {
        return KEY_THEATER_EXISTS + name + ":" + cityId;
    }

    public void save(Theater theater) {
        save(keyTheater(theater.getId()), JsonUtils.toJson(theater), TTL_THEATER);
    }

    public Optional<Theater> getById(long theaterId) {
        return get(keyTheater(theaterId))
            .map(value -> JsonUtils.fromJson(value, Theater.class));
    }

    public Optional<Boolean> exists(String name, long cityId) {
        return get(keyTheaterExists(name, cityId))
            .map(Boolean::parseBoolean);
    }

    public void saveExists(String name, long cityId) {
        save(keyTheaterExists(name, cityId), Boolean.TRUE.toString(), TTL_THEATER_EXISTS);
    }
}
