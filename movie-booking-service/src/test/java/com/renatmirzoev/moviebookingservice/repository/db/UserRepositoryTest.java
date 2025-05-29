package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

@Transactional
class UserRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<User> userOptional = userRepository.getById(Long.MAX_VALUE);
        assertThat(userOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetUserById() {
        User user = ModelUtils.user();
        user = userRepository.save(user);

        Optional<User> userOptional = userRepository.getById(user.getId());

        assertThat(userOptional).isPresent().contains(user);
    }

    @Test
    void shouldNotBeAbleToSaveDuplicate() {
        User user = ModelUtils.user();
        userRepository.save(user);

        assertThatException()
            .isThrownBy(() -> userRepository.save(user))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotExist() {
        boolean userExists = userRepository.exists(UUID.randomUUID().toString());
        assertThat(userExists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenUserExists() {
        User user = ModelUtils.user();
        user = userRepository.save(user);

        boolean userExists = userRepository.exists(user.getEmail());
        assertThat(userExists).isTrue();
    }

}