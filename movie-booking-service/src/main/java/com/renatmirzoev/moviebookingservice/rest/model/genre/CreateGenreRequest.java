package com.renatmirzoev.moviebookingservice.rest.model.genre;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateGenreRequest {
    @NotBlank
    private String name;
}
