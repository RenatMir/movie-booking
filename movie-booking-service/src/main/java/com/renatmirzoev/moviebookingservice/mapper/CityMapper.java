package com.renatmirzoev.moviebookingservice.mapper;

import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.rest.model.city.CreateCityRequest;
import com.renatmirzoev.moviebookingservice.rest.model.city.GetCityResponse;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CityMapper {

    City toCity(CreateCityRequest request);

    GetCityResponse toGetCityResponse(City city);
}
