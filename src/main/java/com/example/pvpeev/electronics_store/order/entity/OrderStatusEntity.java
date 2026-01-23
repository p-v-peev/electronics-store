package com.example.pvpeev.electronics_store.order.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_status")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderStatusEntity {

    @EmbeddedId
    private OrderStatusId id;

    private ZonedDateTime statusUpdateDate;


    @Embeddable
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class OrderStatusId implements Serializable {
        private UUID deliveryStatusId;
        private UUID orderId;
    }
}
