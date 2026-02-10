package com.example.pvpeev.electronics_store.auth.authorities;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AuthoritiesResolverImpl implements AuthoritiesResolver {

    private static final Map<Integer, Authorities> MAP = Arrays.stream(Authorities.values()).collect(Collectors.toMap(Authorities::getId, Function.identity()));

    @Override
    public Authorities resolveById(Integer id) {
        return MAP.get(id);
    }
}
