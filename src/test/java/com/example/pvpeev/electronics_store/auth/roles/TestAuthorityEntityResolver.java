package com.example.pvpeev.electronics_store.auth.roles;

import com.example.pvpeev.electronics_store.auth.entity.AuthorityEntity;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.pvpeev.electronics_store.auth.roles.RoleConstantsp.*;

public class TestAuthorityEntityResolver {

    public static final List<AuthorityEntity> AUTHORITY_ENTITY_STREAM = List.of(
            new AuthorityEntity(1, ROLE_STORE_USER.getAuthority(), "Users registered via the browser with no special permissions."),
            new AuthorityEntity(2, ROLE_STORE_WAREHOUSE_WORKER.getAuthority(), "Workers in the warehouse that can update the status of an order."),
            new AuthorityEntity(3, ROLE_STORE_PRODUCT_ADMIN.getAuthority(), "Administrators that can add or delete new products to the store."),
            new AuthorityEntity(4, ROLE_STORE_AUTHORITY_ADMIN.getAuthority(), "Administrators that grant ot revoke roles the the other users.")
    );

    private static final Map<String, AuthorityEntity> map = AUTHORITY_ENTITY_STREAM.stream().collect(Collectors.toMap(AuthorityEntity::getName, Function.identity()));

    public static AuthorityEntity resolveByRoleConstant(RoleConstantsp constant) {
        final AuthorityEntity authorityResponse = map.get(constant.getAuthority());
        if (authorityResponse == null) {
            throw new IllegalStateException();
        }

        return authorityResponse;
    }
}
