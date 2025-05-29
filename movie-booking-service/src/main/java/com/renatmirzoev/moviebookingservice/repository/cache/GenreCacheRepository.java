package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.model.entity.Genre;
import com.renatmirzoev.moviebookingservice.utils.JsonUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class GenreCacheRepository extends AbstractCacheRepository {

    private static final String KEY_GENRE = "genre:";
    private static final String KEY_GENRE_EXISTS = "genreExists:";

    private static final Duration TTL_GENRE = Duration.ofDays(30);
    private static final Duration TTL_GENRE_EXISTS = Duration.ofDays(30);

    public GenreCacheRepository(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    static String keyGenre(long id) {
        return KEY_GENRE + id;
    }

    static String keyGenreExists(String name) {
        return KEY_GENRE_EXISTS + name;
    }

    public void save(Genre genre) {
        save(keyGenre(genre.getId()), JsonUtils.toJson(genre), TTL_GENRE);
    }

    public Optional<Genre> getById(long id) {
        return get(keyGenre(id))
            .map(value -> JsonUtils.fromJson(value, Genre.class));
    }

    public Optional<Boolean> exists(String name) {
        return get(keyGenreExists(name))
            .map(Boolean::parseBoolean);
    }

    public void saveExists(String name) {
        save(keyGenreExists(name), Boolean.TRUE.toString(), TTL_GENRE_EXISTS);
    }
}
