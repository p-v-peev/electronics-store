package com.example.pvpeev.electronics_store.auth.controller;

import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static com.example.pvpeev.electronics_store.auth.roles.RoleConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorityControllerTest {

    @Autowired
    MockMvcTester mockMvcTester;

    @Test
    public void testGetAllAuthorities() {
        assertThat(mockMvcTester.get().uri(AuthorityController.PATH))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(AuthorityResponse.class))
                .extracting(AuthorityResponse::getName)
                .containsExactlyInAnyOrder(
                        ROLE_STORE_USER.getValue(),
                        ROLE_STORE_WAREHOUSE_WORKER.getValue(),
                        ROLE_STORE_PRODUCT_ADMIN.getValue(),
                        ROLE_STORE_AUTHORITY_ADMIN.getValue()
                );
    }
}
