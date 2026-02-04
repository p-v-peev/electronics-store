package com.example.pvpeev.electronics_store.auth.roles;

import lombok.Getter;

@Getter
public enum RoleConstantsp {
    ROLE_STORE_USER("ROLE_STORE_USER"),
    ROLE_STORE_WAREHOUSE_WORKER("ROLE_STORE_WAREHOUSE_WORKER"),
    ROLE_STORE_PRODUCT_ADMIN("ROLE_STORE_PRODUCT_ADMIN"),
    ROLE_STORE_AUTHORITY_ADMIN("ROLE_STORE_AUTHORITY_ADMIN");

    private final String authority;

    RoleConstantsp(String authority) {
        this.authority = authority;
    }
}
