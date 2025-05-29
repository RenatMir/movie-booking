package com.renatmirzoev.moviebookingservice.rest.model.city;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCityRequest {
    @NotBlank
    private String name;
    @Positive
    private long countryId;
}
