package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.utils.JsonUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class CityCacheRepository extends AbstractCacheRepository {

    private static final String KEY_CITY = "city:";
    private static final String KEY_CITY_EXISTS = "cityExists:";

    private static final Duration TTL_CITY = Duration.ofDays(30);
    private static final Duration TTL_CITY_EXISTS = Duration.ofDays(30);

    public CityCacheRepository(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    static String keyCity(long id) {
        return KEY_CITY + id;
    }

    static String keyCityExists(String name, long countryId) {
        return KEY_CITY_EXISTS + name + ":" + countryId;
    }

    public void save(City city) {
        save(keyCity(city.getId()), JsonUtils.toJson(city), TTL_CITY);
    }

    public Optional<City> getById(long id) {
        return get(keyCity(id))
            .map(value -> JsonUtils.fromJson(value, City.class));
    }

    public Optional<Boolean> exists(String name, long countryId) {
        return get(keyCityExists(name, countryId))
            .map(Boolean::parseBoolean);
    }

    public void saveExists(String name, long countryId) {
        save(keyCityExists(name, countryId), Boolean.TRUE.toString(), TTL_CITY_EXISTS);
    }
}
