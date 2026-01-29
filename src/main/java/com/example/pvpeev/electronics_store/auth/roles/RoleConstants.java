package com.example.pvpeev.electronics_store.auth.roles;

public enum RoleConstants {
    ROLE_STORE_USER("ROLE_STORE_USER"),
    ROLE_STORE_WAREHOUSE_WORKER("ROLE_STORE_WAREHOUSE_WORKER"),
    ROLE_STORE_PRODUCT_ADMIN("ROLE_STORE_PRODUCT_ADMIN"),
    ROLE_STORE_AUTHORITY_ADMIN("ROLE_STORE_AUTHORITY_ADMIN");

    private final String value;

    RoleConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
