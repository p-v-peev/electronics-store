package com.example.pvpeev.electronics_store.auth.roles;

import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.entity.AuthorityEntity;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AuthorityResolverImpl implements AuthorityResolver {
    private final Map<String, AuthorityResponse> map;

    public AuthorityResolverImpl(List<AuthorityEntity> authorityEntities, AuthorityMapper mapper) {
        this.map = authorityEntities.stream().map(mapper::toResponse).collect(Collectors.toMap(AuthorityResponse::getName, Function.identity()));
    }

    public Integer resolveIdByRoleConstant(RoleConstantsp constant) {
        final AuthorityResponse authorityResponse = map.get(constant.getAuthority());
        if (authorityResponse == null) {
            throw new IllegalStateException();
        }

        return authorityResponse.getId();
    }
}
