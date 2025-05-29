package com.renatmirzoev.moviebookingservice.mapper;

import com.renatmirzoev.moviebookingservice.model.entity.Actor;
import com.renatmirzoev.moviebookingservice.model.entity.Genre;
import com.renatmirzoev.moviebookingservice.model.entity.Movie;
import com.renatmirzoev.moviebookingservice.rest.model.movie.CreateMovieRequest;
import com.renatmirzoev.moviebookingservice.rest.model.movie.GetMovieResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface MovieMapper {

    @Mapping(target = "genres", source = "genreIds")
    @Mapping(target = "actors", source = "actorIds")
    Movie toMovie(CreateMovieRequest request);

    GetMovieResponse toGetMovieResponse(Movie movie);

    default Genre map(Long id) {
        if (id == null) {
            return null;
        }
        return new Genre().setId(id);
    }

    default Actor mapActor(Long id) {
        if (id == null) {
            return null;
        }
        return new Actor().setId(id);
    }
}
