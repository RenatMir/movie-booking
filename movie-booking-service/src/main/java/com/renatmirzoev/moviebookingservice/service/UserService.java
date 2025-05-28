package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.exception.UserAlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.User;
import com.renatmirzoev.moviebookingservice.repository.cache.UserCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserCacheRepository userCacheRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public long save(User user) {
        if (userExists(user.getEmail())) {
            throw new UserAlreadyExistsException("User with email %s already exists".formatted(user.getEmail()));
        }

        User savedUser = userRepository.save(user);
        userCacheRepository.save(savedUser);
        return savedUser.getId();
    }

    public Optional<User> getUserById(long id) {
        return userCacheRepository.getById(id)
            .or(() -> {
                Optional<User> userOptional = userRepository.getById(id);
                userOptional.ifPresent(userCacheRepository::save);
                return userOptional;
            });
    }

    public boolean userExists(String email) {
        return userCacheRepository.exists(email)
            .orElseGet(() -> {
                boolean value = userRepository.exists(email);
                userCacheRepository.saveExists(email);
                return value;
            });
    }
}
