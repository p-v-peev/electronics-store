package com.example.pvpeev.electronics_store.auth.authorities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Authority {
    ROLE_STORE_USER(100, "ROLE_STORE_USER", "Users registered via the browser with no special permissions."),
    ROLE_STORE_WAREHOUSE_WORKER(200, "ROLE_STORE_WAREHOUSE_WORKER", "Workers in the warehouse that can prepare orders for shipment."),
    ROLE_STORE_PRODUCT_ADMIN(300, "ROLE_STORE_PRODUCT_ADMIN", "Administrators that can add or delete new products to the store."),
    ROLE_ORDER_STATUS_ADMIN(400, "ROLE_ORDER_STATUS_ADMIN", "Administrators that can manage the order status."),
    ROLE_STORE_AUTHORITY_ADMIN(500, "ROLE_STORE_AUTHORITY_ADMIN", "Administrators that grant ot revoke roles the the other users.");

    private final int id;
    private final String authority;
    private final String description;
}
