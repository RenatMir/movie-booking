package com.renatmirzoev.moviebookingservice.repository;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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
        User user = ModelUtils.createUser();
        user = userRepository.save(user);

        Optional<User> userOptional = userRepository.getById(user.getId());

        assertThat(userOptional).isPresent().contains(user);
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotExist() {
        boolean userExists = userRepository.existsByEmail(UUID.randomUUID().toString());
        assertThat(userExists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenUserExists() {
        User user = ModelUtils.createUser();
        user = userRepository.save(user);

        boolean userExists = userRepository.existsByEmail(user.getEmail());
        assertThat(userExists).isTrue();
    }

}