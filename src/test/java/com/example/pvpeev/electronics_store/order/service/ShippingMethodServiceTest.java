package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.dto.ShippingMethodResponse;
import com.example.pvpeev.electronics_store.order.mapper.ShippingMethodMapper;
import com.example.pvpeev.electronics_store.order.mapper.ShippingMethodMapperImpl;
import com.example.pvpeev.electronics_store.order.shipping.DhlShippingMethodHandler;
import com.example.pvpeev.electronics_store.order.shipping.ShippingMethod;
import com.example.pvpeev.electronics_store.order.shipping.ShippingMethodHandler;
import com.example.pvpeev.electronics_store.order.shipping.SpeedyShippingMethodHandler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ShippingMethodServiceTest {

    @Test
    public void testGetAllShippingMethods() {
        final ShippingMethodMapper shippingMethodMapper = new ShippingMethodMapperImpl();

        final ShippingMethodHandler dhl = new DhlShippingMethodHandler();
        final ShippingMethodHandler speedy = new SpeedyShippingMethodHandler();

        final ShippingMethodService shippingMethodService = new ShippingMethodService(shippingMethodMapper, List.of(dhl, speedy));

        assertThat(shippingMethodService.getAllShippingMethods())
                .containsExactlyInAnyOrder(
                        new ShippingMethodResponse(1, ShippingMethod.DHL.getName()),
                        new ShippingMethodResponse(2, ShippingMethod.SPEEDY.getName())
                );
    }

    @Test
    public void testGetExisitingShippingMethodByName() {
        final ShippingMethodMapper shippingMethodMapper = new ShippingMethodMapperImpl();

        final ShippingMethodHandler dhl = new DhlShippingMethodHandler();

        final ShippingMethodService shippingMethodService = new ShippingMethodService(shippingMethodMapper, List.of(dhl));

        assertThat(shippingMethodService.getShippingMethodByName(ShippingMethod.DHL.getName()))
                .get()
                .isEqualTo(ShippingMethod.DHL);
    }

    @Test
    public void testGetUnexistingShippingMethodByName() {
        final ShippingMethodMapper shippingMethodMapper = new ShippingMethodMapperImpl();

        final ShippingMethodHandler dhl = new DhlShippingMethodHandler();

        final ShippingMethodService shippingMethodService = new ShippingMethodService(shippingMethodMapper, List.of(dhl));

        assertThat(shippingMethodService.getShippingMethodByName("TEST"))
                .isEmpty();
    }
}
