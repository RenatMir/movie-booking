package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.model.entity.User;
import com.renatmirzoev.moviebookingservice.utils.JsonUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class UserCacheRepository extends AbstractCacheRepository {

    private static final String KEY_USER = "user:";
    private static final String KEY_USER_EXISTS_BY_EMAIL = "userExists:";

    private static final Duration TTL_USER = Duration.ofDays(1);
    private static final Duration TTL_USER_EXISTS_BY_EMAIL = Duration.ofDays(1);

    public UserCacheRepository(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    static String keyUser(long userId) {
        return KEY_USER + userId;
    }

    static String keyUserExists(String email) {
        return KEY_USER_EXISTS_BY_EMAIL + email;
    }

    public void save(User user) {
        save(keyUser(user.getId()), JsonUtils.toJson(user), TTL_USER);
    }

    public Optional<User> getById(long userId) {
        return get(keyUser(userId))
            .map(value -> JsonUtils.fromJson(value, User.class));
    }

    public Optional<Boolean> exists(String email) {
        return get(keyUserExists(email))
            .map(Boolean::parseBoolean);
    }

    public void saveExists(String email) {
        save(keyUserExists(email), Boolean.TRUE.toString(), TTL_USER_EXISTS_BY_EMAIL);
    }
}
