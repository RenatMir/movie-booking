package com.renatmirzoev.moviebookingservice.rest.model.actor;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateActorRequest {
    @NotBlank
    private String fullName;
}
