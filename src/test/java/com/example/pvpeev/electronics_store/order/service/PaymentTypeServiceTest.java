package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.dto.PaymentTypeResponse;
import com.example.pvpeev.electronics_store.order.mapper.PaymentTypeMapper;
import com.example.pvpeev.electronics_store.order.mapper.PaymentTypeMapperImpl;
import com.example.pvpeev.electronics_store.order.payment.DebitCardVisaPaymentHandler;
import com.example.pvpeev.electronics_store.order.payment.PaymentOnDeliveryHandler;
import com.example.pvpeev.electronics_store.order.payment.PaymentType;
import com.example.pvpeev.electronics_store.order.payment.PaymentTypeHandler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentTypeServiceTest {

    @Test
    public void testGetAllPaymentTypes() {
        final PaymentTypeMapper paymentTypeMapper = new PaymentTypeMapperImpl();

        final PaymentTypeHandler debitCardPaymentHandler = new DebitCardVisaPaymentHandler(null, null);
        final PaymentTypeHandler paymentOnDeliveryHandler = new PaymentOnDeliveryHandler(null, null, null, List.of());

        final PaymentTypeService paymentTypeService = new PaymentTypeService(paymentTypeMapper, List.of(debitCardPaymentHandler, paymentOnDeliveryHandler));

        assertThat(paymentTypeService.getAllPaymentTypes())
                .containsExactlyInAnyOrder(
                        new PaymentTypeResponse(1, PaymentType.PAYMENT_ON_DELIVERY.getName(), PaymentType.PAYMENT_ON_DELIVERY.getDescription()),
                        new PaymentTypeResponse(2, PaymentType.DEBIT_CARD_VISA.getName(), PaymentType.DEBIT_CARD_VISA.getDescription())
                );
    }

    @Test
    public void testGetExistingPaymentPaymentTypes() {
        final PaymentTypeMapper paymentTypeMapper = new PaymentTypeMapperImpl();

        final PaymentTypeHandler debitCardPaymentHandler = new DebitCardVisaPaymentHandler(null, null);

        final PaymentTypeService paymentTypeService = new PaymentTypeService(paymentTypeMapper, List.of(debitCardPaymentHandler));

        assertThat(paymentTypeService.getPaymentTypeByName(PaymentType.DEBIT_CARD_VISA.getName()))
                .get()
                .isEqualTo(PaymentType.DEBIT_CARD_VISA);
    }

    @Test
    public void testGetUnexistingPaymentPaymentTypes() {
        final PaymentTypeMapper paymentTypeMapper = new PaymentTypeMapperImpl();

        final PaymentTypeHandler debitCardPaymentHandler = new DebitCardVisaPaymentHandler(null, null);

        final PaymentTypeService paymentTypeService = new PaymentTypeService(paymentTypeMapper, List.of(debitCardPaymentHandler));

        assertThat(paymentTypeService.getPaymentTypeByName("TEST"))
                .isEmpty();
    }
}
