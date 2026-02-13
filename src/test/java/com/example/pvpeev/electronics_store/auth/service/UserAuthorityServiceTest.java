package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.advice.exception.BadRequestException;
import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.auth.authorities.Authority;
import com.example.pvpeev.electronics_store.auth.authorities.AuthoritiesResolver;
import com.example.pvpeev.electronics_store.auth.authorities.AuthoritiesResolverImpl;
import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.entity.UserAuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapper;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapperImpl;
import com.example.pvpeev.electronics_store.auth.repository.UserAuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.pvpeev.electronics_store.auth.authorities.Authority.ROLE_STORE_USER;
import static com.example.pvpeev.electronics_store.auth.authorities.Authority.ROLE_STORE_WAREHOUSE_WORKER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAuthorityServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAuthorityRepository userAuthorityRepository;

    @Spy
    private AuthoritiesResolver authoritiesResolver = new AuthoritiesResolverImpl();

    @Spy
    private AuthorityMapper authorityMapper = new AuthorityMapperImpl();

    @InjectMocks
    private UserAuthorityService userAuthorityService;


    @Test
    public void testFindAllByIdUnexistingUserThrowsException() {
        final UUID userId = UUID.randomUUID();

        when(userRepository.findByIdAndEnabledIsTrue(userId)).thenReturn(Optional.empty());

        final RuntimeException exception = catchRuntimeException(() -> userAuthorityService.findAllByUserId(userId));
        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404")
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, times(1)).findByIdAndEnabledIsTrue(userId);
        verify(userAuthorityRepository, times(0)).findAllByUserId(userId);
        verify(authoritiesResolver, times(0)).resolveById(anyInt());
        verify(authorityMapper, times(0)).toResponse(any(Authority.class));
    }

    @Test
    public void testFindAllByIdReturnsListOfAuthorityResponse() {
        final UserEntity userEntity = new UserEntity(UUID.randomUUID(), "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true);

        when(userRepository.findByIdAndEnabledIsTrue(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(userAuthorityRepository.findAllByUserId(userEntity.getId())).thenReturn(List.of(new UserAuthorityEntity(1L, userEntity.getId(), ROLE_STORE_USER.getId())));

        assertThat(userAuthorityService.findAllByUserId(userEntity.getId()))
                .as("The list  size must be exactly one")
                .singleElement()
                .as("The response doesn't match the expected response")
                .isEqualTo(new AuthorityResponse(ROLE_STORE_USER.getId(), ROLE_STORE_USER.getAuthority(), ROLE_STORE_USER.getDescription()));

        verify(userRepository, times(1)).findByIdAndEnabledIsTrue(userEntity.getId());
        verify(userAuthorityRepository, times(1)).findAllByUserId(userEntity.getId());
        verify(authoritiesResolver, times(1)).resolveById(ROLE_STORE_USER.getId());
        verify(authorityMapper, times(1)).toResponse(ROLE_STORE_USER);
    }

    @Test
    public void testGrantUserAuthority() {
        final UUID userId = UUID.randomUUID();

        assertThat(userAuthorityService.grantUserAuthority(userId, ROLE_STORE_USER.getId()))
                .as("The response doesn't match the expected response")
                .isEqualTo(new AuthorityResponse(ROLE_STORE_USER.getId(), ROLE_STORE_USER.getAuthority(), ROLE_STORE_USER.getDescription()));

        verify(userAuthorityRepository, times(1)).save(new UserAuthorityEntity(null, userId, ROLE_STORE_USER.getId()));
        verify(authoritiesResolver, times(1)).resolveById(ROLE_STORE_USER.getId());
        verify(authorityMapper, times(1)).toResponse(ROLE_STORE_USER);
    }

    @Test
    public void testRevokeRoleUserThrowsException() {
        final UUID userId = UUID.randomUUID();

        final RuntimeException exception = catchRuntimeException(() -> userAuthorityService.revokeUserAuthority(userId, ROLE_STORE_USER.getId()));
        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404")
                .isInstanceOf(BadRequestException.class);

        verify(authoritiesResolver, times(1)).resolveById(ROLE_STORE_USER.getId());
        verify(userAuthorityRepository, times(0)).deleteByUserIdAndAuthorityId(userId, ROLE_STORE_USER.getId());
    }

    @Test
    public void testRevokeAuthorityUnexistingUserThrowsException() {
        final UUID userId = UUID.randomUUID();

        when(userAuthorityRepository.deleteByUserIdAndAuthorityId(userId, ROLE_STORE_WAREHOUSE_WORKER.getId())).thenReturn(0);

        final RuntimeException exception = catchRuntimeException(() -> userAuthorityService.revokeUserAuthority(userId, ROLE_STORE_WAREHOUSE_WORKER.getId()));
        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404")
                .isInstanceOf(ResourceNotFoundException.class);

        verify(authoritiesResolver, times(1)).resolveById(ROLE_STORE_WAREHOUSE_WORKER.getId());
        verify(userAuthorityRepository, times(1)).deleteByUserIdAndAuthorityId(userId, ROLE_STORE_WAREHOUSE_WORKER.getId());
    }

    @Test
    public void testRevokeAuthority() {
        final UUID userId = UUID.randomUUID();

        when(userAuthorityRepository.deleteByUserIdAndAuthorityId(userId, ROLE_STORE_WAREHOUSE_WORKER.getId())).thenReturn(1);

        userAuthorityService.revokeUserAuthority(userId, ROLE_STORE_WAREHOUSE_WORKER.getId());

        verify(authoritiesResolver, times(1)).resolveById(ROLE_STORE_WAREHOUSE_WORKER.getId());
        verify(userAuthorityRepository, times(1)).deleteByUserIdAndAuthorityId(userId, ROLE_STORE_WAREHOUSE_WORKER.getId());
    }

}
