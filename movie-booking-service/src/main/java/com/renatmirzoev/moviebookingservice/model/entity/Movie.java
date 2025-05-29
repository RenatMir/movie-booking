package com.renatmirzoev.moviebookingservice.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.Set;

@Data
@Accessors(chain = true)
public class Movie {
    private long id;
    private String name;
    private String description;
    private Set<Genre> genres;
    private Set<Actor> actors;
    private Instant dateCreated;
}
