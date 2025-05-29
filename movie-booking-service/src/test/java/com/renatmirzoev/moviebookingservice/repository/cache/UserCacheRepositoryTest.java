package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class UserCacheRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private UserCacheRepository userCacheRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<User> userOptional = userCacheRepository.getById(Long.MAX_VALUE);
        assertThat(userOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetUserById() {
        User user = ModelUtils.user();
        userCacheRepository.save(user);

        Optional<User> userOptional = userCacheRepository.getById(user.getId());
        assertThat(userOptional).isPresent().contains(user);
    }

    @Test
    void shouldReturnEmptyWhenUserDoesNotExist() {
        Optional<Boolean> exists = userCacheRepository.exists(UUID.randomUUID().toString());
        assertThat(exists).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenUserExists() {
        String value = UUID.randomUUID().toString();
        userCacheRepository.saveExists(value);

        Optional<Boolean> userExists = userCacheRepository.exists(value);
        assertThat(userExists).isPresent().contains(true);
    }
}