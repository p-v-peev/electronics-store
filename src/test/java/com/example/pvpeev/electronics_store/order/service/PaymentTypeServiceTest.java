package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.dto.PaymentTypeResponse;
import com.example.pvpeev.electronics_store.order.mapper.PaymentTypeMapper;
import com.example.pvpeev.electronics_store.order.mapper.PaymentTypeMapperImpl;
import com.example.pvpeev.electronics_store.order.payment.DebitCardPaymentHandler;
import com.example.pvpeev.electronics_store.order.payment.PaymentOnDeliveryHandler;
import com.example.pvpeev.electronics_store.order.payment.PaymentTypeHandler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentTypeServiceTest {

    @Test
    public void testGetPaymentTypes() {
        final PaymentTypeMapper paymentTypeMapper = new PaymentTypeMapperImpl();

        final PaymentTypeHandler debitCardPaymentHandler = new DebitCardPaymentHandler(null, null);
        final PaymentTypeHandler paymentOnDeliveryHandler = new PaymentOnDeliveryHandler(null, null, null, List.of());

        final PaymentTypeService paymentTypeService = new PaymentTypeService(paymentTypeMapper, List.of(debitCardPaymentHandler, paymentOnDeliveryHandler));

        assertThat(paymentTypeService.getAllPaymentTypes())
                .containsExactlyInAnyOrder(
                        new PaymentTypeResponse(1, "Payment on delivery", "Pay when the product is delivered."),
                        new PaymentTypeResponse(2, "Debit card payment with VISA", "Pay with your VISA debit card")
                );
    }
}
