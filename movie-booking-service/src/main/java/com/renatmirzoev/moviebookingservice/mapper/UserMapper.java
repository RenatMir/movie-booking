package com.renatmirzoev.moviebookingservice.mapper;

import com.renatmirzoev.moviebookingservice.model.entity.User;
import com.renatmirzoev.moviebookingservice.rest.model.user.CreateUserRequest;
import com.renatmirzoev.moviebookingservice.rest.model.user.GetUserResponse;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    User toUser(CreateUserRequest request);

    GetUserResponse toGetUserResponse(User user);
}
