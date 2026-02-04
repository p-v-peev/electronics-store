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

import static com.example.pvpeev.electronics_store.auth.roles.RoleConstantsp.ROLE_STORE_USER;
import static org.assertj.core.api.Assertions.assertThatList;
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
        final AuthorityEntity entity = new AuthorityEntity(1, ROLE_STORE_USER.getAuthority(), "Users registered via the browser with no special permissions.");

        when(authorityRepository.findAll()).thenReturn(List.of(entity));


        final AuthorityResponse expectedResponse = new AuthorityResponse(entity.getId(), entity.getName(), entity.getDescription());

        assertThatList(authorityService.findAll())
                .as("The service must return exactly one authority")
                .singleElement()
                .as("The response doesn't match the expected response")
                .isEqualTo(expectedResponse);

        verify(authorityRepository, times(1)).findAll();
        verify(authorityMapper, times(1)).toResponse(entity);
    }

}
