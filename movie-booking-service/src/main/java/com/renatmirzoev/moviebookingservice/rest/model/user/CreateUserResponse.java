package com.renatmirzoev.moviebookingservice.rest.model.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateUserResponse {
    private long id;
}
