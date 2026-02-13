package com.example.pvpeev.electronics_store.auth.controller;

import com.example.pvpeev.electronics_store.auth.dto.*;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapper;
import com.example.pvpeev.electronics_store.auth.repository.UserAddressRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserAuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static com.example.pvpeev.electronics_store.auth.authorities.Authority.ROLE_STORE_USER;
import static com.example.pvpeev.electronics_store.auth.authorities.Authority.ROLE_STORE_WAREHOUSE_WORKER;
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
    private AuthorityMapper authorityMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void afterEachTest() {
        userAddressRepository.deleteAll();
        userAuthorityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testCreateRetrieveUser() {
        final UserRequest userRequest = getUserRequestInstance();

        final MvcTestResult response = mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest))
                .exchange();

        assertThat(response)
                .hasStatus(HttpStatus.CREATED)
                .containsHeader(HttpHeaders.LOCATION);

        final String location = response.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(location).isNotNull();

        assertThat(mockMvcTester.get()
                .uri(location))
                .hasStatusOk()
                .bodyJson()
                .convertTo(UserResponse.class)
                .satisfies(userResponse -> {
                    assertThat(userResponse.getId()).isNotNull();
                    assertThat(userRequest.getEmail()).isEqualTo(userResponse.getEmail());
                    assertThat(userRequest.getFirstName()).isEqualTo(userResponse.getFirstName());
                    assertThat(userRequest.getLastName()).isEqualTo(userResponse.getLastName());
                    assertThat(userRequest.getPhoneNumber()).isEqualTo(userResponse.getPhoneNumber());
                });
    }

    @Test
    public void testCreateDeleteUser() {
        final UserRequest userRequest = getUserRequestInstance();

        final MvcTestResult response = mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest))
                .exchange();

        final String location = response.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(location).isNotNull();

        assertThat(mockMvcTester.delete()
                .uri(location))
                .hasStatus(HttpStatus.NO_CONTENT);

        assertThat(mockMvcTester.get()
                .uri(location))
                .hasStatus(HttpStatus.NOT_FOUND);

        final UUID userId = UUID.fromString(location.substring(location.lastIndexOf('/') + 1));

        assertThat(userRepository.findById(userId)).get().extracting(UserEntity::isEnabled).isEqualTo(false);
        assertThat(userRepository.findByIdAndEnabledIsTrue(userId)).isEmpty();
        assertThat(userRepository.findUserAuthByEmail(userRequest.getEmail())).isEmpty();
        assertThat(userAuthorityRepository.findAllByUserId(userId)).isEmpty();
    }


    @Test
    public void testPostTheSameUserTwiceReturnsConflict() {
        final UserRequest userRequest = getUserRequestInstance();

        assertThat(mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest)))
                .hasStatus(HttpStatus.CREATED);

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
    public void testAddAddressToUnexistingUserReturnsBadRequest() {
        final UserAddressRequest addressRequest = new UserAddressRequest("Test address 1");
        assertThat(mockMvcTester.post()
                .uri(UserController.PATH + "/" + UUID.randomUUID() + "/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(addressRequest)))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testNewUserHasEmptyAddresses() {
        final UserRequest userRequest = getUserRequestInstance();

        final MvcTestResult response = mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest))
                .exchange();

        final String location = response.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(location).isNotNull();

        assertThat(mockMvcTester.get()
                .uri(location + "/addresses"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(UserAddressResponse.class))
                .isEmpty();
    }

    @Test
    public void testCreateRetrieveUserAddress() {
        final UserRequest userRequest = getUserRequestInstance();

        final MvcTestResult userResponse = mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest))
                .exchange();

        final String userLocation = userResponse.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(userLocation).isNotNull();

        final UserAddressRequest addressRequest = new UserAddressRequest("Test address 1");
        final MvcTestResult createAddressResponse = mockMvcTester.post()
                .uri(userLocation + "/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(addressRequest))
                .exchange();

        assertThat(createAddressResponse)
                .hasStatus(HttpStatus.CREATED)
                .containsHeader(HttpHeaders.LOCATION)
                .bodyJson()
                .convertTo(UserAddressResponse.class)
                .satisfies(response -> {
                    assertThat(response.getUserId().toString()).isEqualTo(userLocation.substring(userLocation.lastIndexOf('/') + 1));
                    assertThat(response.getAddress()).isEqualTo(addressRequest.getAddress());
                });

        final String addressLocation = createAddressResponse.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(addressLocation).isNotNull();

        assertThat(mockMvcTester.get()
                .uri(addressLocation))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(UserAddressResponse.class))
                .singleElement()
                .satisfies(response -> {
                    assertThat(response.getUserId().toString()).isEqualTo(userLocation.substring(userLocation.lastIndexOf('/') + 1));
                    assertThat(response.getAddress()).isEqualTo(addressRequest.getAddress());
                });
    }

    @Test
    public void testDeleteUserDeletesAllUserAddresses() {
        final UserRequest userRequest = getUserRequestInstance();

        final MvcTestResult userResponse = mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest))
                .exchange();

        final String userLocation = userResponse.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(userLocation).isNotNull();

        final UserAddressRequest addressRequest = new UserAddressRequest("Test address 1");
        assertThat(mockMvcTester.post()
                .uri(userLocation + "/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(addressRequest)))
                .hasStatus(HttpStatus.CREATED);

        assertThat(mockMvcTester.delete()
                .uri(userLocation))
                .hasStatus(HttpStatus.NO_CONTENT);

        assertThat(mockMvcTester.get()
                .uri(userLocation + "/addresses"))
                .hasStatus(HttpStatus.NOT_FOUND);

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
    public void testAddAuthorityToUnexistingUserReturnsBadRequest() {
        assertThat(mockMvcTester.post()
                .uri(UserController.PATH + "/" + UUID.randomUUID() + "/authorities/2"))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testDeleteAuthorityToUnexistingUserReturnsNotFound() {
        assertThat(mockMvcTester.delete()
                .uri(UserController.PATH + "/" + UUID.randomUUID() + "/authorities/" + ROLE_STORE_WAREHOUSE_WORKER.getId()))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testNewUserHasTheStoreUserAuthority() {
        final UserRequest userRequest = getUserRequestInstance();

        final MvcTestResult response = mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest))
                .exchange();

        final String userLocation = response.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(userLocation).isNotNull();

        assertThat(mockMvcTester.get()
                .uri(userLocation + "/authorities"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(AuthorityResponse.class))
                .singleElement()
                .isEqualTo(authorityMapper.toResponse(ROLE_STORE_USER));

        final String userId = userLocation.substring(userLocation.lastIndexOf('/') + 1);
        assertThat(userAuthorityRepository.findAllByUserId(UUID.fromString(userId))).hasSize(1);
    }

    @Test
    public void testCreateRetrieveUserAuthorities() {
        final UserRequest userRequest = getUserRequestInstance();

        final MvcTestResult userResponse = mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest))
                .exchange();

        final String userLocation = userResponse.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(userLocation).isNotNull();

        final MvcTestResult createAuthorityResponse = mockMvcTester.post()
                .uri(userLocation + "/authorities/" + ROLE_STORE_WAREHOUSE_WORKER.getId())
                .exchange();

        assertThat(createAuthorityResponse)
                .hasStatus(HttpStatus.CREATED)
                .containsHeader(HttpHeaders.LOCATION)
                .bodyJson()
                .convertTo(AuthorityResponse.class)
                .isEqualTo(authorityMapper.toResponse(ROLE_STORE_WAREHOUSE_WORKER));


        assertThat(mockMvcTester.get()
                .uri(userLocation + "/authorities"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(AuthorityResponse.class))
                .hasSize(2)
                .containsExactlyInAnyOrder(authorityMapper.toResponse(ROLE_STORE_USER), authorityMapper.toResponse(ROLE_STORE_WAREHOUSE_WORKER));

        final String userId = userLocation.substring(userLocation.lastIndexOf('/') + 1);
        assertThat(userAuthorityRepository.findAllByUserId(UUID.fromString(userId))).hasSize(2);
    }

    @Test
    public void testDeleteUserAuthority() {
        final UserRequest userRequest = getUserRequestInstance();

        final MvcTestResult userResponse = mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest))
                .exchange();

        final String userLocation = userResponse.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(userLocation).isNotNull();

        assertThat(mockMvcTester.post()
                .uri(userLocation + "/authorities/" + ROLE_STORE_WAREHOUSE_WORKER.getId()))
                .hasStatus(HttpStatus.CREATED);

        assertThat(mockMvcTester.delete()
                .uri(userLocation + "/authorities/" + ROLE_STORE_WAREHOUSE_WORKER.getId()))
                .hasStatus(HttpStatus.NO_CONTENT);

        assertThat(mockMvcTester.get()
                .uri(userLocation + "/authorities"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(AuthorityResponse.class))
                .hasSize(1);

        final String userId = userLocation.substring(userLocation.lastIndexOf('/') + 1);
        assertThat(userAuthorityRepository.findAllByUserId(UUID.fromString(userId))).hasSize(1);
    }

    @Test
    public void testDeleteRoleStoreUserAuthorityReturnsBadRequest() {
        final UserRequest userRequest = getUserRequestInstance();

        final MvcTestResult userResponse = mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest))
                .exchange();

        final String userLocation = userResponse.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(userLocation).isNotNull();

        assertThat(mockMvcTester.delete()
                .uri(userLocation + "/authorities/" + ROLE_STORE_USER.getId()))
                .hasStatus(HttpStatus.BAD_REQUEST);

        final String userId = userLocation.substring(userLocation.lastIndexOf('/') + 1);
        assertThat(userAuthorityRepository.findAllByUserId(UUID.fromString(userId))).hasSize(1);
    }

    @Test
    public void testDeleteUserDeletesAllUserAuthorities() {
        final UserRequest userRequest = getUserRequestInstance();

        final MvcTestResult userResponse = mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest))
                .exchange();

        final String userLocation = userResponse.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(userLocation).isNotNull();

        assertThat(mockMvcTester.post()
                .uri(userLocation + "/authorities/" + ROLE_STORE_WAREHOUSE_WORKER.getId()))
                .hasStatus(HttpStatus.CREATED);

        assertThat(mockMvcTester.delete()
                .uri(userLocation))
                .hasStatus(HttpStatus.NO_CONTENT);

        assertThat(mockMvcTester.get()
                .uri(userLocation + "/authorities"))
                .hasStatus(HttpStatus.NOT_FOUND);

        final String userId = userLocation.substring(userLocation.lastIndexOf('/') + 1);
        assertThat(userAuthorityRepository.findAllByUserId(UUID.fromString(userId))).isEmpty();
    }

    @Test
    public void testGrantUserUnexistingAuthorityReturnsBadRequest() {
        final UserRequest userRequest = getUserRequestInstance();

        final MvcTestResult userResponse = mockMvcTester.post()
                .uri(UserController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userRequest))
                .exchange();

        final String userLocation = userResponse.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(userLocation).isNotNull();

        assertThat(mockMvcTester.post()
                .uri(userLocation + "/authorities/-1"))
                .hasStatus(HttpStatus.BAD_REQUEST);

        final String userId = userLocation.substring(userLocation.lastIndexOf('/') + 1);
        assertThat(userAuthorityRepository.findAllByUserId(UUID.fromString(userId)))
                .as("The user must have only the ROLE_STORE_USER")
                .singleElement()
                .satisfies(entity -> {
                    assertThat(entity.getUserId().toString()).isEqualTo(userId);
                    assertThat(entity.getAuthorityId()).isEqualTo(ROLE_STORE_USER.getId());
                });
    }

    private static @NotNull UserRequest getUserRequestInstance() {
        return new UserRequest("pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213");
    }
}
