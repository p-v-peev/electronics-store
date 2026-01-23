package com.example.pvpeev.electronics_store.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "order")
public class OrderEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID user_id;

    private String orderAddress;

    private String orderZipCode;

    private ZonedDateTime orderDate;

    private UUID paymentTypeId;

    private String phoneNumber;

    private String trackingCode;

    private UUID shippingMethodId;
}
