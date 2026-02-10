package com.example.pvpeev.electronics_store.auth.authorities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Authorities {
    ROLE_STORE_USER(1, "ROLE_STORE_USER", "Users registered via the browser with no special permissions."),
    ROLE_STORE_WAREHOUSE_WORKER(2, "ROLE_STORE_WAREHOUSE_WORKER", "Workers in the warehouse that can update the status of an order."),
    ROLE_STORE_PRODUCT_ADMIN(3, "ROLE_STORE_PRODUCT_ADMIN", "Administrators that can add or delete new products to the store."),
    ROLE_STORE_AUTHORITY_ADMIN(4, "ROLE_STORE_AUTHORITY_ADMIN", "Administrators that grant ot revoke roles the the other users.");

    private final int id;
    private final String authority;
    private final String description;
}
