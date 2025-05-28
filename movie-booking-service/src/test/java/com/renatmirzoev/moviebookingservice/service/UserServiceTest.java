package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.exception.UserAlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.User;
import com.renatmirzoev.moviebookingservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    void shouldNotSaveUserIfUserExists() {
        User user = ModelUtils.createUser();

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatException()
            .isThrownBy(() -> userService.save(user))
            .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldSaveUser() {
        User user = ModelUtils.createUser();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.save(user);

        verify(userRepository).existsByEmail(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldGetEmptyUserById() {
        when(userRepository.getById(anyLong())).thenReturn(Optional.empty());

        Optional<User> userOptional = userService.getUserById(Long.MAX_VALUE);
        assertThat(userOptional).isEmpty();
    }

    @Test
    void shouldGetUserById() {
        User user = ModelUtils.createUser();

        when(userRepository.getById(anyLong())).thenReturn(Optional.of(user));

        Optional<User> userOptional = userService.getUserById(Long.MAX_VALUE);
        assertThat(userOptional).isPresent().contains(user);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldReturnFalseWhenUserDoesNotExist(boolean existParam) {
        when(userRepository.existsByEmail(anyString())).thenReturn(existParam);

        boolean exists = userService.userExistsByEmail(UUID.randomUUID().toString());

        assertThat(exists).isEqualTo(existParam);
    }

}