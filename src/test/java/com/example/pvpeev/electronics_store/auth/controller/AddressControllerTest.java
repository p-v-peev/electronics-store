package com.example.pvpeev.electronics_store.auth.controller;

import com.example.pvpeev.electronics_store.auth.entity.UserAddressEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.auth.repository.UserAddressRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void afterEachTest() {
        userAddressRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testDeleteUnexistingAddressReturnsNotFound() {
        assertThat(mockMvcTester.delete().uri(AddressController.PATH + "/{id}", 1L))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteExistingAddressReturnsNoContent() {
        final UUID userId = userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true)).getId();
        final Long addressId = userAddressRepository.save(new UserAddressEntity(null, userId, "Test address")).getId();

        assertThat(mockMvcTester.delete()
                .uri(AddressController.PATH + "/{id}", addressId))
                .hasStatus(HttpStatus.NO_CONTENT);

        assertThat(userAddressRepository.existsById(addressId)).isFalse();
    }
}
