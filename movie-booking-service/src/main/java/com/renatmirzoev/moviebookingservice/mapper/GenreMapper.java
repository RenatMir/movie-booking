package com.renatmirzoev.moviebookingservice.mapper;

import com.renatmirzoev.moviebookingservice.model.entity.Genre;
import com.renatmirzoev.moviebookingservice.rest.model.genre.CreateGenreRequest;
import com.renatmirzoev.moviebookingservice.rest.model.genre.GetGenreResponse;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface GenreMapper {

    Genre toGenre(CreateGenreRequest request);

    GetGenreResponse toGetGenreResponse(Genre genre);
}
