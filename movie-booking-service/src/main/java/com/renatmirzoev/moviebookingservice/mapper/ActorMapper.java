package com.renatmirzoev.moviebookingservice.mapper;

import com.renatmirzoev.moviebookingservice.model.entity.Actor;
import com.renatmirzoev.moviebookingservice.rest.model.actor.CreateActorRequest;
import com.renatmirzoev.moviebookingservice.rest.model.actor.GetActorResponse;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ActorMapper {

    Actor toActor(CreateActorRequest request);

    GetActorResponse toGetActorResponse(Actor actor);
}
