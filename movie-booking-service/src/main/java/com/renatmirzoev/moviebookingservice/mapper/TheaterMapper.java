package com.renatmirzoev.moviebookingservice.mapper;

import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import com.renatmirzoev.moviebookingservice.rest.model.theater.CreateTheaterRequest;
import com.renatmirzoev.moviebookingservice.rest.model.theater.GetTheaterResponse;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface TheaterMapper {

    Theater toTheater(CreateTheaterRequest request);

    GetTheaterResponse toGetTheaterResponse(Theater theater);
}
