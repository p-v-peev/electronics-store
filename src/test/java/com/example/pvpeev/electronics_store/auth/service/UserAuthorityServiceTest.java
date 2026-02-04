package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.advice.exception.BadRequestException;
import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.entity.UserAuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapper;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapperImpl;
import com.example.pvpeev.electronics_store.auth.repository.AuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserAuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import com.example.pvpeev.electronics_store.auth.roles.TestAuthorityEntityResolver;
import com.example.pvpeev.electronics_store.auth.roles.TestAuthorityResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.pvpeev.electronics_store.auth.roles.RoleConstantsp.ROLE_STORE_USER;
import static com.example.pvpeev.electronics_store.auth.roles.RoleConstantsp.ROLE_STORE_WAREHOUSE_WORKER;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAuthorityServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private UserAuthorityRepository userAuthorityRepository;

    @Spy
    private TestAuthorityResolver authorityResolver = new TestAuthorityResolver();

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
    }

    @Test
    public void testFindAllByIdReturnsListOfAuthorityResponse() {
        final UserEntity userEntity = new UserEntity(UUID.randomUUID(), "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true);
        final Integer authorityId = authorityResolver.resolveIdByRoleConstant(ROLE_STORE_USER);

        when(userRepository.findByIdAndEnabledIsTrue(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(userAuthorityRepository.findAllByUserId(userEntity.getId())).thenReturn(List.of(new UserAuthorityEntity(1L, userEntity.getId(), authorityId)));
        when(authorityRepository.findAllById(List.of(authorityId))).thenReturn(List.of(TestAuthorityEntityResolver.resolveByRoleConstant(ROLE_STORE_USER)));

        assertThatList(userAuthorityService.findAllByUserId(userEntity.getId()))
                .as("The list  size must be exactly one")
                .singleElement()
                .as("The response doesn't match the expected response")
                .isEqualTo(authorityResolver.resolveByRoleConstant(ROLE_STORE_USER));

        verify(userRepository, times(1)).findByIdAndEnabledIsTrue(userEntity.getId());
        verify(userAuthorityRepository, times(1)).findAllByUserId(userEntity.getId());
        verify(authorityRepository).findAllById(List.of(authorityId));
    }

    @Test
    public void testGrantUserAuthority() {
        final UUID userId = UUID.randomUUID();
        final Integer authorityId = authorityResolver.resolveIdByRoleConstant(ROLE_STORE_USER);

        when(authorityRepository.findById(authorityId)).thenReturn(Optional.of(TestAuthorityEntityResolver.resolveByRoleConstant(ROLE_STORE_USER)));

        final AuthorityResponse expectedResponse = authorityResolver.resolveByRoleConstant(ROLE_STORE_USER);
        assertThat(userAuthorityService.grantUserAuthority(userId, authorityId))
                .as("The response doesn't match the expected response")
                .isEqualTo(expectedResponse);

        verify(userAuthorityRepository, times(1)).save(new UserAuthorityEntity(null, userId, authorityId));
        verify(authorityRepository, times(1)).findById(authorityId);
        verify(authorityMapper, times(1)).toResponse(TestAuthorityEntityResolver.resolveByRoleConstant(ROLE_STORE_USER));
    }

    @Test
    public void testRevokeRoleUserThrowsException() {
        final UUID userId = UUID.randomUUID();
        final Integer authorityId = authorityResolver.resolveIdByRoleConstant(ROLE_STORE_USER);

        final RuntimeException exception = catchRuntimeException(() -> userAuthorityService.revokeUserAuthority(userId, authorityId));
        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404")
                .isInstanceOf(BadRequestException.class);

        verify(userAuthorityRepository, times(0)).deleteByUserIdAndAuthorityId(userId, authorityId);
    }

    @Test
    public void testRevokeAuthorityUnexistingUserThrowsException() {
        final UUID userId = UUID.randomUUID();
        final Integer authorityId = authorityResolver.resolveIdByRoleConstant(ROLE_STORE_WAREHOUSE_WORKER);

        when(userAuthorityRepository.deleteByUserIdAndAuthorityId(userId, authorityId)).thenReturn(0);

        final RuntimeException exception = catchRuntimeException(() -> userAuthorityService.revokeUserAuthority(userId, authorityId));
        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404")
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userAuthorityRepository, times(1)).deleteByUserIdAndAuthorityId(userId, authorityId);
    }

    @Test
    public void testRevokeAuthority() {
        final UUID userId = UUID.randomUUID();
        final Integer authorityId = authorityResolver.resolveIdByRoleConstant(ROLE_STORE_WAREHOUSE_WORKER);

        when(userAuthorityRepository.deleteByUserIdAndAuthorityId(userId, authorityId)).thenReturn(1);

        userAuthorityService.revokeUserAuthority(userId, authorityId);

        verify(userAuthorityRepository, times(1)).deleteByUserIdAndAuthorityId(userId, authorityId);
    }

}
