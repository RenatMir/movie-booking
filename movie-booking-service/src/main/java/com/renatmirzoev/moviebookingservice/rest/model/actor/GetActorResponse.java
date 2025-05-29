package com.renatmirzoev.moviebookingservice.rest.model.actor;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetActorResponse {
    private long id;
    private String fullName;
}
