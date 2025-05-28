package com.renatmirzoev.moviebookingservice.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class Theater {
    private long id;
    private String name;
    private long cityId;
    private Instant dateCreated;
}
