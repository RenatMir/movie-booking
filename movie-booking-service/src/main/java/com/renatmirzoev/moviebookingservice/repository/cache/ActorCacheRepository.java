package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.model.entity.Actor;
import com.renatmirzoev.moviebookingservice.utils.JsonUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class ActorCacheRepository extends AbstractCacheRepository {

    private static final String KEY_ACTOR = "actor:";
    private static final String KEY_ACTOR_EXISTS = "actorExists:";

    private static final Duration TTL_ACTOR = Duration.ofDays(30);
    private static final Duration TTL_ACTOR_EXISTS = Duration.ofDays(30);

    public ActorCacheRepository(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    static String keyActor(long actorId) {
        return KEY_ACTOR + actorId;
    }

    static String keyActorExists(String fullName) {
        return KEY_ACTOR_EXISTS + fullName;
    }

    public void save(Actor actor) {
        save(keyActor(actor.getId()), JsonUtils.toJson(actor), TTL_ACTOR);
    }

    public Optional<Actor> getById(long actorId) {
        return get(keyActor(actorId))
            .map(value -> JsonUtils.fromJson(value, Actor.class));
    }

    public Optional<Boolean> exists(String fullName) {
        return get(keyActorExists(fullName))
            .map(Boolean::parseBoolean);
    }

    public void saveExists(String fullName) {
        save(keyActorExists(fullName), Boolean.TRUE.toString(), TTL_ACTOR_EXISTS);
    }
}
