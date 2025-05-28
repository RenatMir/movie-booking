package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.exception.UserAlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.User;
import com.renatmirzoev.moviebookingservice.repository.UserRepository;
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

    @Transactional(propagation = Propagation.REQUIRED)
    public long save(User user) {
        if (userExistsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User with email %s already exists".formatted(user.getEmail()));
        }

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Optional<User> getUserById(long id) {
        return userRepository.getById(id);
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
