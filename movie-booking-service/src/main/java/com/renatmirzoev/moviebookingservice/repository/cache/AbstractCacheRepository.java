package com.renatmirzoev.moviebookingservice.repository.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class AbstractCacheRepository {

    private static final String KEY_PREFIX = "MovieBooking:";

    private final StringRedisTemplate stringRedisTemplate;

    private static String key(String key) {
        return KEY_PREFIX + key;
    }

    protected Optional<String> get(String key) {
        String value = stringRedisTemplate.opsForValue().get(key(key));
        return Optional.ofNullable(value);
    }

    protected void save(String key, String value, Duration ttl) {
        stringRedisTemplate.opsForValue().set(key(key), value, ttl);
    }
}
