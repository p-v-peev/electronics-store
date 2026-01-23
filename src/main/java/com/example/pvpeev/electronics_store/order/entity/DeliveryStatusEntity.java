package com.example.pvpeev.electronics_store.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "delivery_status")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DeliveryStatusEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
}
