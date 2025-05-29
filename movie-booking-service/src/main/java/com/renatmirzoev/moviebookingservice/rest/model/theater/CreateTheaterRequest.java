package com.renatmirzoev.moviebookingservice.rest.model.theater;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateTheaterRequest {
    @NotBlank
    private String name;
    @Positive
    private long cityId;
}
