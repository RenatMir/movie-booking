package com.renatmirzoev.moviebookingservice.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class Genre {
    private long id;
    private String name;
    private Instant dateCreated;
}
