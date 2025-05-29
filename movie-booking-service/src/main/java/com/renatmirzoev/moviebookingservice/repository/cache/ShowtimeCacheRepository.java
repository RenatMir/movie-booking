package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.model.entity.Showtime;
import com.renatmirzoev.moviebookingservice.utils.JsonUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Repository
public class ShowtimeCacheRepository extends AbstractCacheRepository {

    private static final String KEY_SHOWTIME = "showtime:";
    private static final String KEY_SHOWTIME_EXISTS = "showtimeExists:";

    private static final Duration TTL_SHOWTIME = Duration.ofMinutes(15);
    private static final Duration TTL_SHOWTIME_EXISTS = Duration.ofMinutes(15);

    public ShowtimeCacheRepository(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    static String keyShowtime(long showtimeId) {
        return KEY_SHOWTIME + showtimeId;
    }

    static String keyShowtimeExists(long movieId, long theaterId, Instant dateShow) {
        return KEY_SHOWTIME_EXISTS + movieId + ":" + theaterId + ":" + dateShow;
    }

    public void save(Showtime showtime) {
        save(keyShowtime(showtime.getId()), JsonUtils.toJson(showtime), TTL_SHOWTIME);
    }

    public Optional<Showtime> getById(long showtimeId) {
        return get(keyShowtime(showtimeId))
            .map(value -> JsonUtils.fromJson(value, Showtime.class));
    }

    public Optional<Boolean> exists(long movieId, long theaterId, Instant dateShow) {
        return get(keyShowtimeExists(movieId, theaterId, dateShow))
            .map(Boolean::parseBoolean);
    }

    public void saveExists(long movieId, long theaterId, Instant dateShow) {
        save(keyShowtimeExists(movieId, theaterId, dateShow), Boolean.TRUE.toString(), TTL_SHOWTIME_EXISTS);
    }
}
