--liquibase formatted sql
--changeset pvpeev:add-store-roles

INSERT INTO authority (name, description) VALUES
    ('ROLE_STORE_USER', 'Users registered via the browser with no special permissions.'),
    ('ROLE_STORE_WAREHOUSE_WORKER', 'Workers in the warehouse that can update the status of an order.'),
    ('ROLE_STORE_PRODUCT_ADMIN', 'Administrators that can add or delete new products to the store'),
    ('ROLE_STORE_AUTHORITY_ADMIN', 'Administrators that grant ot revoke roles the the other users')