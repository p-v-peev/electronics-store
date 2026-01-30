package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.entity.AuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserAuthorityEntity;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapper;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapperImpl;
import com.example.pvpeev.electronics_store.auth.repository.AuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserAuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import com.example.pvpeev.electronics_store.auth.roles.RoleConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
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
    private AuthorityMapper authorityMapper = new AuthorityMapperImpl();

    @InjectMocks
    private UserAuthorityService userAuthorityService;

    @Test
    public void testFindAllByIdUnexistingUserThrowsException() {
        final UUID userId = UUID.randomUUID();

        when(userRepository.existsById(eq(userId))).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> userAuthorityService.findAllByUserId(userId),
                "Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404");

        verify(userRepository, times(1)).existsById(eq(userId));
    }

    @Test
    public void testFindAllByIdReturnsListOfAuthorityResponse() {
        final UUID userId = UUID.randomUUID();
        final String authorityDescription = "Regular store user with no specific authorities";

        when(userRepository.existsById(eq(userId))).thenReturn(true);
        when(userAuthorityRepository.findAllByUserId(eq(userId))).thenReturn(List.of(new UserAuthorityEntity(1L, userId, 1)));
        when(authorityRepository.findAllById(eq(List.of(1)))).thenReturn(List.of(new AuthorityEntity(1, RoleConstants.ROLE_STORE_USER.getValue(), authorityDescription)));

        final List<AuthorityResponse> responseList = userAuthorityService.findAllByUserId(userId);
        assertEquals(1, responseList.size(), "The response size must be exactly one");
        final AuthorityResponse firstResponse = responseList.getFirst();
        final AuthorityResponse expectedResponse = new AuthorityResponse(1, RoleConstants.ROLE_STORE_USER.getValue(), authorityDescription);
        assertEquals(expectedResponse, firstResponse, "The response doesn't match the expected response");

    }

    @Test
    public void testGrantUserAuthority() {
        final UUID userId = UUID.randomUUID();
        final String authorityDescription = "Regular store user with no specific authorities";
        final AuthorityEntity authorityEntity = new AuthorityEntity(1, RoleConstants.ROLE_STORE_USER.getValue(), authorityDescription);

        when(authorityRepository.findById(eq(1))).thenReturn(Optional.of(authorityEntity));

        final AuthorityResponse response = userAuthorityService.grantUserAuthority(userId, 1);
        final AuthorityResponse expectedResponse = new AuthorityResponse(1, RoleConstants.ROLE_STORE_USER.getValue(), authorityDescription);
        assertEquals(expectedResponse, response, "The response doesn't match the expected response");

        verify(userAuthorityRepository, times(1)).save(eq(new UserAuthorityEntity(null, userId, 1)));
        verify(authorityRepository, times(1)).findById(eq(1));
        verify(authorityMapper, times(1)).toResponse(eq(authorityEntity));
    }

    @Test
    public void testRevokeAuthorityUnexistingUserThrowsException() {
        final UUID userId = UUID.randomUUID();

        when(userAuthorityRepository.deleteByUserIdAndAuthorityId(eq(userId), eq(1))).thenReturn(0);

        assertThrows(ResourceNotFoundException.class,
                () -> userAuthorityService.revokeUserAuthority(userId, 1),
                "Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404");

        verify(userAuthorityRepository, times(1)).deleteByUserIdAndAuthorityId(eq(userId), eq(1));
    }

    @Test
    public void testRevokeAuthority() {
        final UUID userId = UUID.randomUUID();

        when(userAuthorityRepository.deleteByUserIdAndAuthorityId(eq(userId), eq(1))).thenReturn(1);

        userAuthorityService.revokeUserAuthority(userId, 1);

        verify(userAuthorityRepository, times(1)).deleteByUserIdAndAuthorityId(eq(userId), eq(1));
    }

}
