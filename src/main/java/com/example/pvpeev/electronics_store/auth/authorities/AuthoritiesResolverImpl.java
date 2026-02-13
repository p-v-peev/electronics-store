package com.example.pvpeev.electronics_store.auth.authorities;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AuthoritiesResolverImpl implements AuthoritiesResolver {

    private static final Map<Integer, Authority> MAP = Arrays.stream(Authority.values()).collect(Collectors.toMap(Authority::getId, Function.identity()));

    @Override
    public Authority resolveById(Integer id) {
        return MAP.get(id);
    }
}
