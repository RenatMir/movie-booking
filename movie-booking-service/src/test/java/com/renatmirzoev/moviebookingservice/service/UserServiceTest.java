package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.exception.AlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.User;
import com.renatmirzoev.moviebookingservice.repository.cache.UserCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserCacheRepository userCacheRepository;
    @InjectMocks
    private UserService userService;

    private InOrder inOrder;

    @BeforeEach
    void init() {
        inOrder = Mockito.inOrder(userRepository, userCacheRepository);
    }

    @Test
    void shouldNotSaveUserIfUserExistsFromCache() {
        User user = ModelUtils.user();

        when(userCacheRepository.exists(anyString())).thenReturn(Optional.of(true));

        assertThatException()
            .isThrownBy(() -> userService.saveUser(user))
            .isInstanceOf(AlreadyExistsException.class);

        inOrder.verify(userCacheRepository).exists(anyString());
        inOrder.verify(userCacheRepository, never()).save(any(User.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldNotSaveUserIfUserExistsFromDb() {
        User user = ModelUtils.user();

        when(userCacheRepository.exists(anyString())).thenReturn(Optional.empty());
        when(userRepository.exists(anyString())).thenReturn(true);

        assertThatException()
            .isThrownBy(() -> userService.saveUser(user))
            .isInstanceOf(AlreadyExistsException.class);

        inOrder.verify(userCacheRepository).exists(anyString());
        inOrder.verify(userRepository).exists(anyString());
        inOrder.verify(userCacheRepository).saveExists(anyString());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldSaveUser() {
        User user = ModelUtils.user();

        when(userCacheRepository.exists(anyString())).thenReturn(Optional.empty());
        when(userRepository.exists(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.saveUser(user);

        inOrder.verify(userCacheRepository).exists(anyString());
        inOrder.verify(userRepository).exists(anyString());
        inOrder.verify(userCacheRepository).saveExists(anyString());
        inOrder.verify(userRepository).save(any(User.class));
        inOrder.verify(userCacheRepository).save(any(User.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetEmptyUserById() {
        when(userCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.getById(anyLong())).thenReturn(Optional.empty());

        Optional<User> userOptional = userService.getUserById(Long.MAX_VALUE);
        assertThat(userOptional).isEmpty();

        inOrder.verify(userCacheRepository).getById(anyLong());
        inOrder.verify(userRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetUserByIdFromCache() {
        User user = ModelUtils.user();

        when(userCacheRepository.getById(anyLong())).thenReturn(Optional.of(user));

        Optional<User> userOptional = userService.getUserById(Long.MAX_VALUE);
        assertThat(userOptional).isPresent().contains(user);

        inOrder.verify(userCacheRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetUserByIdFromDB() {
        User user = ModelUtils.user();

        when(userCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.getById(anyLong())).thenReturn(Optional.of(user));

        Optional<User> userOptional = userService.getUserById(Long.MAX_VALUE);
        assertThat(userOptional).isPresent().contains(user);

        inOrder.verify(userCacheRepository).getById(anyLong());
        inOrder.verify(userRepository).getById(anyLong());
        inOrder.verify(userCacheRepository).save(any(User.class));
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromDb(boolean existParam) {
        when(userCacheRepository.exists(anyString())).thenReturn(Optional.empty());
        when(userRepository.exists(anyString())).thenReturn(existParam);

        boolean exists = userService.userExists(UUID.randomUUID().toString());

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(userCacheRepository).exists(anyString());
        inOrder.verify(userRepository).exists(anyString());
        inOrder.verify(userCacheRepository).saveExists(anyString());
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromCache(boolean existParam) {
        when(userCacheRepository.exists(anyString())).thenReturn(Optional.of(existParam));

        boolean exists = userService.userExists(UUID.randomUUID().toString());

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(userCacheRepository).exists(anyString());
        inOrder.verifyNoMoreInteractions();
    }

}