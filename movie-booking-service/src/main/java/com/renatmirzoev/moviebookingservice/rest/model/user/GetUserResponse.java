package com.renatmirzoev.moviebookingservice.rest.model.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetUserResponse {
    private long id;
    private String fullName;
    private String email;
    private String phoneNumber;
}
