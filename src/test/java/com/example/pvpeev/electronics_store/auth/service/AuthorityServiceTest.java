package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapper;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.pvpeev.electronics_store.auth.authorities.Authority.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthorityServiceTest {

    @Spy
    private AuthorityMapper authorityMapper = new AuthorityMapperImpl();

    @InjectMocks
    private AuthorityService authorityService;

    @Test
    void testFindAllAuthorities() {
        assertThat(authorityService.findAll())
                .hasSize(4)
                .containsExactlyInAnyOrder(
                        new AuthorityResponse(ROLE_STORE_USER.getId(), ROLE_STORE_USER.getAuthority(), ROLE_STORE_USER.getDescription()),
                        new AuthorityResponse(ROLE_STORE_WAREHOUSE_WORKER.getId(), ROLE_STORE_WAREHOUSE_WORKER.getAuthority(), ROLE_STORE_WAREHOUSE_WORKER.getDescription()),
                        new AuthorityResponse(ROLE_STORE_AUTHORITY_ADMIN.getId(), ROLE_STORE_AUTHORITY_ADMIN.getAuthority(), ROLE_STORE_AUTHORITY_ADMIN.getDescription()),
                        new AuthorityResponse(ROLE_STORE_PRODUCT_ADMIN.getId(), ROLE_STORE_PRODUCT_ADMIN.getAuthority(), ROLE_STORE_PRODUCT_ADMIN.getDescription())
                );

        verify(authorityMapper, times(1)).toResponse(ROLE_STORE_USER);
        verify(authorityMapper, times(1)).toResponse(ROLE_STORE_WAREHOUSE_WORKER);
        verify(authorityMapper, times(1)).toResponse(ROLE_STORE_AUTHORITY_ADMIN);
        verify(authorityMapper, times(1)).toResponse(ROLE_STORE_PRODUCT_ADMIN);
    }

}
