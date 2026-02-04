package com.example.pvpeev.electronics_store.auth.roles;

import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapper;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapperImpl;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestAuthorityResolver implements AuthorityResolver {
    private final Map<String, AuthorityResponse> map;

    public TestAuthorityResolver() {
        final AuthorityMapper authorityMapper = new AuthorityMapperImpl();
        this.map = TestAuthorityEntityResolver.AUTHORITY_ENTITY_STREAM
                .stream()
                .map(authorityMapper::toResponse)
                .collect(Collectors.toMap(AuthorityResponse::getName, Function.identity()));
    }

    public AuthorityResponse resolveByRoleConstant(RoleConstantsp constant) {
        final AuthorityResponse authorityResponse = map.get(constant.getAuthority());
        if (authorityResponse == null) {
            throw new IllegalStateException();
        }

        return authorityResponse;
    }

    public Integer resolveIdByRoleConstant(RoleConstantsp constant) {
        return resolveByRoleConstant(constant).getId();
    }
}
