package com.renatmirzoev.moviebookingservice.mapper;

import com.renatmirzoev.moviebookingservice.model.entity.Showtime;
import com.renatmirzoev.moviebookingservice.rest.model.showtime.CreateShowtimeRequest;
import com.renatmirzoev.moviebookingservice.rest.model.showtime.GetShowtimeResponse;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ShowtimeMapper {

    Showtime toShowtime(CreateShowtimeRequest request);

    GetShowtimeResponse toGetShowtimeResponse(Showtime showtime);
}
