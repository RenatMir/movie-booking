package com.renatmirzoev.moviebookingservice.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public abstract class AbstractEntity {
    private Instant dateCreated;
}
