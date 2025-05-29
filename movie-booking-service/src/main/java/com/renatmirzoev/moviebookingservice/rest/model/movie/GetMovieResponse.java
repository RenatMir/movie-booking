package com.renatmirzoev.moviebookingservice.rest.model.movie;

import com.renatmirzoev.moviebookingservice.rest.model.actor.ActorPublic;
import com.renatmirzoev.moviebookingservice.rest.model.genre.GenrePublic;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class GetMovieResponse {
    private long id;
    private String name;
    private String description;
    private Set<GenrePublic> genres;
    private Set<ActorPublic> actors;
}
