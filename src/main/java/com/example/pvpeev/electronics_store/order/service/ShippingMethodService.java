package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.dto.ShippingMethodResponse;
import com.example.pvpeev.electronics_store.order.mapper.ShippingMethodMapper;
import com.example.pvpeev.electronics_store.order.shipping.ShippingMethod;
import com.example.pvpeev.electronics_store.order.shipping.ShippingMethodHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ShippingMethodService {

    private final ShippingMethodMapper shippingMethodMapper;
    private final Map<Integer, ShippingMethodHandler> availableHandlers;
    private final Map<String, ShippingMethod> shippingMethodsByName;

    public ShippingMethodService(ShippingMethodMapper shippingMethodMapper, List<ShippingMethodHandler> handlers) {
        this.shippingMethodMapper = shippingMethodMapper;
        this.availableHandlers = handlers.stream().collect(Collectors.toMap(shippingMethodHandler -> shippingMethodHandler.getSupportedShippingMethod().getId(), Function.identity()));
        this.shippingMethodsByName = handlers.stream().collect(Collectors.toMap(smh -> smh.getSupportedShippingMethod().name(), ShippingMethodHandler::getSupportedShippingMethod));
    }


    public List<ShippingMethodResponse> getAllShippingMethods() {
        return availableHandlers.values().
                stream()
                .map(ShippingMethodHandler::getSupportedShippingMethod)
                .map(shippingMethodMapper::toResponse)
                .toList();
    }

    public Optional<ShippingMethod> getShippingMethodByName(String name) {
        return Optional.ofNullable(shippingMethodsByName.get(name));
    }

    public ShippingMethodHandler getHandlerById(Integer id) {
        return availableHandlers.get(id);
    }
}
