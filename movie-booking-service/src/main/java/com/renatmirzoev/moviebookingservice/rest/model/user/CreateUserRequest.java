package com.renatmirzoev.moviebookingservice.rest.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateUserRequest {
    @NotBlank
    private String fullName;
    @Email
    @NotBlank
    private String email;
    private String phoneNumber;
}
