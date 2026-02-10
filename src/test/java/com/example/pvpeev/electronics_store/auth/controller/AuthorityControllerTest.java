package com.example.pvpeev.electronics_store.auth.controller;

import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static com.example.pvpeev.electronics_store.auth.authorities.Authorities.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorityControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;


    @Test
    public void testGetAllAuthorities() {
        assertThat(mockMvcTester.get().uri(AuthorityController.PATH))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(AuthorityResponse.class))
                .hasSize(4)
                .containsExactlyInAnyOrder(
                        new AuthorityResponse(ROLE_STORE_USER.getId(), ROLE_STORE_USER.getAuthority(), ROLE_STORE_USER.getDescription()),
                        new AuthorityResponse(ROLE_STORE_WAREHOUSE_WORKER.getId(), ROLE_STORE_WAREHOUSE_WORKER.getAuthority(), ROLE_STORE_WAREHOUSE_WORKER.getDescription()),
                        new AuthorityResponse(ROLE_STORE_PRODUCT_ADMIN.getId(), ROLE_STORE_PRODUCT_ADMIN.getAuthority(), ROLE_STORE_PRODUCT_ADMIN.getDescription()),
                        new AuthorityResponse(ROLE_STORE_AUTHORITY_ADMIN.getId(), ROLE_STORE_AUTHORITY_ADMIN.getAuthority(), ROLE_STORE_AUTHORITY_ADMIN.getDescription())
                );
    }
}
