package com.renatmirzoev.moviebookingservice.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class User {
    private long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Instant dateCreated;
}
