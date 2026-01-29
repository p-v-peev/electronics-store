package com.example.pvpeev.electronics_store.auth.mapper;

import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.entity.AuthorityEntity;
import org.mapstruct.Mapper;

@Mapper
public interface AuthorityMapper {

    AuthorityResponse toResponse(AuthorityEntity entity);

}
