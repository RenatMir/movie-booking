package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.model.entity.Movie;
import com.renatmirzoev.moviebookingservice.utils.JsonUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class MovieCacheRepository extends AbstractCacheRepository {

    private static final String KEY_MOVIE = "movie:";

    private static final Duration TTL_MOVIE = Duration.ofDays(1);

    public MovieCacheRepository(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    static String keyMovie(long id) {
        return KEY_MOVIE + id;
    }

    public void save(Movie movie) {
        save(keyMovie(movie.getId()), JsonUtils.toJson(movie), TTL_MOVIE);
    }

    public Optional<Movie> getById(long id) {
        return get(keyMovie(id))
            .map(value -> JsonUtils.fromJson(value, Movie.class));
    }
}
