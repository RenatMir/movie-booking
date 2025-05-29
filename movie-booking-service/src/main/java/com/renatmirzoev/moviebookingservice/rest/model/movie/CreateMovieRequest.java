package com.renatmirzoev.moviebookingservice.rest.model.movie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class CreateMovieRequest {
    @NotBlank
    private String name;
    private String description;
    private Set<@Positive Long> genreIds;
    private Set<@Positive Long> actorIds;
}
