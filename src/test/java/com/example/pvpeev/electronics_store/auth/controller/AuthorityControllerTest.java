package com.example.pvpeev.electronics_store.auth.controller;

import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.roles.RoleConstantsp;
import com.example.pvpeev.electronics_store.auth.roles.TestAuthorityResolver;
import com.example.pvpeev.electronics_store.auth.roles.TestAuthorityResolverConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestAuthorityResolverConfiguration.class)
public class AuthorityControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private TestAuthorityResolver authorityResolver;


    @Test
    public void testGetAllAuthorities() {
        assertThat(mockMvcTester.get().uri(AuthorityController.PATH))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(AuthorityResponse.class))
                .hasSize(4)
                .containsExactlyInAnyOrder(
                        authorityResolver.resolveByRoleConstant(RoleConstantsp.ROLE_STORE_USER),
                        authorityResolver.resolveByRoleConstant(RoleConstantsp.ROLE_STORE_WAREHOUSE_WORKER),
                        authorityResolver.resolveByRoleConstant(RoleConstantsp.ROLE_STORE_PRODUCT_ADMIN),
                        authorityResolver.resolveByRoleConstant(RoleConstantsp.ROLE_STORE_AUTHORITY_ADMIN)
                );
    }
}
