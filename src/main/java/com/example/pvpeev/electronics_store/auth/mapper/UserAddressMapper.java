package com.example.pvpeev.electronics_store.auth.mapper;

import com.example.pvpeev.electronics_store.auth.dto.UserAddressRequest;
import com.example.pvpeev.electronics_store.auth.dto.UserAddressResponse;
import com.example.pvpeev.electronics_store.auth.entity.UserAddressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public interface UserAddressMapper {

    UserAddressResponse toResponse(UserAddressEntity entity);

    @Mapping(target = "id", ignore = true)
    UserAddressEntity toEntity(UserAddressRequest request, UUID userId);

}
