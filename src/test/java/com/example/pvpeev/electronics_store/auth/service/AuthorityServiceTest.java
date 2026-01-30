package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.entity.AuthorityEntity;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapper;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapperImpl;
import com.example.pvpeev.electronics_store.auth.repository.AuthorityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.example.pvpeev.electronics_store.auth.roles.RoleConstants.ROLE_STORE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorityServiceTest {

    @Mock
    private AuthorityRepository authorityRepository;

    @Spy
    private AuthorityMapper authorityMapper = new AuthorityMapperImpl();

    @InjectMocks
    private AuthorityService authorityService;

    @Test
    void testFindAllAuthorities() {
        final Integer authorityId = 1;
        final String authorityDescription = "Regular store user with no specific authorities";
        final AuthorityEntity entity = new AuthorityEntity(authorityId, ROLE_STORE_USER.getValue(), authorityDescription);

        when(authorityRepository.findAll()).thenReturn(List.of(entity));

        final List<AuthorityResponse> authorities = authorityService.findAll();
        assertEquals(1, authorities.size(), "The service must return exactly one authority");
        final AuthorityResponse response = authorities.getFirst();

        final AuthorityResponse expectedResponse = new AuthorityResponse(authorityId, ROLE_STORE_USER.getValue(), authorityDescription);
        assertEquals(expectedResponse, response, "The response doesn't match the expected response");

        verify(authorityRepository, times(1)).findAll();
        verify(authorityMapper, times(1)).toResponse(eq(entity));
    }

}
