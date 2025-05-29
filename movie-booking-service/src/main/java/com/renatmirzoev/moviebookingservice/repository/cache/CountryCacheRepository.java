package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.utils.JsonUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class CountryCacheRepository extends AbstractCacheRepository {

    private static final String KEY_COUNTRY = "country:";
    private static final String KEY_COUNTRY_EXISTS = "countryExists:";

    private static final Duration TTL_COUNTRY = Duration.ofDays(30);
    private static final Duration TTL_COUNTRY_EXISTS = Duration.ofDays(30);

    public CountryCacheRepository(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    static String keyCountry(long id) {
        return KEY_COUNTRY + id;
    }

    static String keyCountryExists(String name) {
        return KEY_COUNTRY_EXISTS + name;
    }

    public void save(Country country) {
        save(keyCountry(country.getId()), JsonUtils.toJson(country), TTL_COUNTRY);
    }

    public Optional<Country> getById(long id) {
        return get(keyCountry(id))
            .map(value -> JsonUtils.fromJson(value, Country.class));
    }

    public Optional<Boolean> exists(String name) {
        return get(keyCountryExists(name))
            .map(Boolean::parseBoolean);
    }

    public void saveExists(String name) {
        save(keyCountryExists(name), Boolean.TRUE.toString(), TTL_COUNTRY_EXISTS);
    }
}
