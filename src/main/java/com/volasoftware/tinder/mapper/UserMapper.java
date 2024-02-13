package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.dto.FriendProfileDto;
import com.volasoftware.tinder.dto.UserDto;
import com.volasoftware.tinder.dto.UserProfileDto;
import com.volasoftware.tinder.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "role", expression = "java(com.volasoftware.tinder.model.Role.USER)")
    @Mapping(target = "friends", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User userDtoToUser(UserDto userDto);

    UserProfileDto userToUserProfileDto(User user);

    FriendProfileDto userToFriendProfileDto(User user);
}
