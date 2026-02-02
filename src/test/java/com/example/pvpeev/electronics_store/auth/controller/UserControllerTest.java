package com.example.pvpeev.electronics_store.auth.controller;

import com.example.pvpeev.electronics_store.auth.dto.*;
import com.example.pvpeev.electronics_store.auth.repository.UserAddressRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserAuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.UUID;

import static com.example.pvpeev.electronics_store.auth.roles.RoleConstants.ROLE_STORE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    public void testCreateDeleteUser() {
        final UserRequest userRequest = getUserRequestInstance();

        final MvcTestResult createUserResponse = mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest))
                .exchange();

        assertThat(createUserResponse)
                .hasStatus(HttpStatus.CREATED)
                .containsHeader(HttpHeaders.LOCATION);

        final String location = createUserResponse.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(location).isNotNull();

        assertThat(mockMvcTester.get()
                .uri(URI.create(location).getPath()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(UserResponse.class)
                .satisfies(user -> {
                    assertThat(userRequest.getEmail()).isEqualTo(user.getEmail());
                    assertThat(userRequest.getFirstName()).isEqualTo(user.getFirstName());
                    assertThat(userRequest.getLastName()).isEqualTo(user.getLastName());
                    assertThat(userRequest.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
                });

        assertThat(mockMvcTester.delete()
                .uri(URI.create(location).getPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest)))
                .hasStatus(HttpStatus.NO_CONTENT);

        assertThat(mockMvcTester.get()
                .uri(URI.create(location).getPath()))
                .hasStatus(HttpStatus.NOT_FOUND);

        final UUID userId = UUID.fromString(location.substring(location.lastIndexOf('/') + 1));

        assertThat(userRepository.findById(userId)).get().matches(user -> !user.isEnabled());
        assertThat(userAuthorityRepository.findAllByUserId(userId)).isEmpty();
    }


    @Test
    @Transactional
    public void testPostTheSameUserTwiceReturnsConflict() {
        final UserRequest userRequest = getUserRequestInstance();

        final MvcTestResult createUserResponse = mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest))
                .exchange();

        final String location = createUserResponse.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(location).isNotNull();

        assertThat(mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest)))
                .hasStatus(HttpStatus.CONFLICT);
    }

    @Test
    public void testUserAddressesReturnNotFoundOnUnexistingUser() {
        assertThat(mockMvcTester.get()
                .uri(UserController.PATH + "/" + UUID.randomUUID() + "/addresses"))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @Transactional
    public void testUserAddresses() {
        final UserRequest userRequest = getUserRequestInstance();

        final MvcTestResult createUserResponse = mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest))
                .exchange();

        final String userLocation = createUserResponse.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(userLocation).isNotNull();
        final String userPath = URI.create(userLocation).getPath();

        assertThat(mockMvcTester.get()
                .uri(userPath + "/addresses"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(UserAddressResponse.class))
                .isEmpty();

        final UserAddressRequest addressRequest = new UserAddressRequest("Test address 1");
        final MvcTestResult createAddressResponse = mockMvcTester.post()
                .uri(userPath + "/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(addressRequest))
                .exchange();

        assertThat(createAddressResponse)
                .hasStatus(HttpStatus.CREATED)
                .containsHeader(HttpHeaders.LOCATION);

        final String addressLocation = createAddressResponse.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(addressLocation).isNotNull();

        assertThat(mockMvcTester.get()
                .uri(URI.create(addressLocation).getPath()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(UserAddressResponse.class))
                .singleElement()
                .extracting(UserAddressResponse::getAddress)
                .isEqualTo(addressRequest.getAddress());

        assertThat(mockMvcTester.delete()
                .uri(userPath))
                .hasStatus(HttpStatus.NO_CONTENT);
        final UUID userId = UUID.fromString(userLocation.substring(userLocation.lastIndexOf('/') + 1));

        assertThat(userAddressRepository.findAllByUserId(userId)).isEmpty();

    }

    @Test
    public void testUserAuthoritiesReturnsNotFoundOnUnexistingUser() {
        assertThat(mockMvcTester.get()
                .uri(UserController.PATH + "/" + UUID.randomUUID() + "/authorities"))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @Transactional
    public void testAddAuthorityToUnexistingUserReturnsBadRequest() {
        assertThat(mockMvcTester.post()
                .uri(UserController.PATH + "/" + UUID.randomUUID() + "/authorities/2"))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Transactional
    public void testDeleteAuthorityToUnexistingUserReturnsNotFound() {
        assertThat(mockMvcTester.delete()
                .uri(UserController.PATH + "/" + UUID.randomUUID() + "/authorities/2"))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @Transactional
    public void testUserAuthorities() {
        final UserRequest userRequest = getUserRequestInstance();

        final MvcTestResult createUserResponse = mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest))
                .exchange();

        final String userLocation = createUserResponse.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(userLocation).isNotNull();
        final String userPath = URI.create(userLocation).getPath();

        assertThat(mockMvcTester.get()
                .uri(userPath + "/authorities"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(AuthorityResponse.class))
                .singleElement()
                .extracting(AuthorityResponse::getName)
                .isEqualTo(ROLE_STORE_USER.getValue());

        assertThat(mockMvcTester.post()
                .uri(userPath + "/authorities/2"))
                .hasStatus(HttpStatus.CREATED)
                .containsHeader(HttpHeaders.LOCATION);

        assertThat(mockMvcTester.get()
                .uri(userPath + "/authorities"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(AuthorityResponse.class))
                .hasSize(2);

        assertThat(mockMvcTester.delete()
                .uri(userPath + "/authorities/2"))
                .hasStatus(HttpStatus.NO_CONTENT);

        assertThat(mockMvcTester.get()
                .uri(userPath + "/authorities"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(AuthorityResponse.class))
                .hasSize(1);

        assertThat(mockMvcTester.delete()
                .uri(userPath))
                .hasStatus(HttpStatus.NO_CONTENT);
        final UUID userId = UUID.fromString(userLocation.substring(userLocation.lastIndexOf('/') + 1));

        assertThat(userAuthorityRepository.findAllByUserId(userId)).isEmpty();
    }

    private static @NotNull UserRequest getUserRequestInstance() {
        return new UserRequest("pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213");
    }
}
