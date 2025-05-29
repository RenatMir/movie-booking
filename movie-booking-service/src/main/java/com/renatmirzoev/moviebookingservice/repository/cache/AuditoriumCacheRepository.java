package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.model.entity.Auditorium;
import com.renatmirzoev.moviebookingservice.utils.JsonUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class AuditoriumCacheRepository extends AbstractCacheRepository {

    private static final String KEY_AUDITORIUM = "auditorium:";
    private static final String KEY_AUDITORIUM_EXISTS = "auditoriumExists:";

    private static final Duration TTL_AUDITORIUM = Duration.ofDays(1);
    private static final Duration TTL_AUDITORIUM_EXISTS = Duration.ofDays(1);

    public AuditoriumCacheRepository(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    static String keyAuditorium(long id) {
        return KEY_AUDITORIUM + id;
    }

    static String keyAuditoriumExists(String name, long theaterId) {
        return KEY_AUDITORIUM_EXISTS + theaterId + ":" + name;
    }


    public void save(Auditorium auditorium) {
        save(keyAuditorium(auditorium.getId()), JsonUtils.toJson(auditorium), TTL_AUDITORIUM);
    }

    public Optional<Auditorium> getById(long id) {
        return get(keyAuditorium(id))
            .map(value -> JsonUtils.fromJson(value, Auditorium.class));
    }

    public Optional<Boolean> exists(String name, long theaterId) {
        return get(keyAuditoriumExists(name, theaterId))
            .map(Boolean::parseBoolean);
    }

    public void saveExists(String name, long theaterId) {
        save(keyAuditoriumExists(name, theaterId), Boolean.TRUE.toString(), TTL_AUDITORIUM_EXISTS);
    }

}
