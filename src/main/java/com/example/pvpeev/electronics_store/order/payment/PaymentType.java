package com.example.pvpeev.electronics_store.order.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentType {
    PAYMENT_ON_DELIVERY(1, "Payment on delivery", "Pay when the product is delivered."),
    DEBIT_CARD_VISA(2, "Visa", "Pay with your VISA debit card.");

    private final int id;
    private final String displayName;
    private final String description;

}
