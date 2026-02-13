package com.example.pvpeev.electronics_store.auth.mapper;

import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.authorities.Authority;
import org.mapstruct.Mapper;

@Mapper
public interface AuthorityMapper {

    AuthorityResponse toResponse(Authority authority);

}
