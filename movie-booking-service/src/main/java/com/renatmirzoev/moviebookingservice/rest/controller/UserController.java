package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.exception.notfound.UserNotFoundException;
import com.renatmirzoev.moviebookingservice.mapper.UserMapper;
import com.renatmirzoev.moviebookingservice.model.entity.User;
import com.renatmirzoev.moviebookingservice.rest.model.user.CreateUserRequest;
import com.renatmirzoev.moviebookingservice.rest.model.user.CreateUserResponse;
import com.renatmirzoev.moviebookingservice.rest.model.user.GetUserResponse;
import com.renatmirzoev.moviebookingservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public ResponseEntity<GetUserResponse> getUser(@PathVariable("id") long id) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with id %s not found".formatted(id));
        }

        User user = userOptional.get();
        GetUserResponse response = userMapper.toGetUserResponse(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userMapper.toUser(request);
        long id = userService.saveUser(user);

        CreateUserResponse response = new CreateUserResponse()
            .setId(id);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
