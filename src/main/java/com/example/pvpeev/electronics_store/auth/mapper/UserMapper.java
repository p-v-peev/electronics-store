package com.example.pvpeev.electronics_store.auth.mapper;

import com.example.pvpeev.electronics_store.auth.dto.UserRequest;
import com.example.pvpeev.electronics_store.auth.dto.UserResponse;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    UserResponse toResponse(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", expression = "java(request.getEmail().toLowerCase())")
    UserEntity toEntity(UserRequest request, boolean enabled);

}
