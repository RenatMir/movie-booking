package com.renatmirzoev.moviebookingservice.rest.model.country;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCountryRequest {
    @NotBlank
    private String name;
}
