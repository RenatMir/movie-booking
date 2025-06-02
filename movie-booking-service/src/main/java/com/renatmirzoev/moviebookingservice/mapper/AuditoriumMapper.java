package com.renatmirzoev.moviebookingservice.mapper;

import com.renatmirzoev.moviebookingservice.model.entity.Auditorium;
import com.renatmirzoev.moviebookingservice.rest.model.auditorium.CreateAuditoriumRequest;
import com.renatmirzoev.moviebookingservice.rest.model.auditorium.GetAuditoriumResponse;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AuditoriumMapper {

    Auditorium toAuditorium(CreateAuditoriumRequest request);

    GetAuditoriumResponse toGetAuditoriumResponse(Auditorium auditorium);
}
