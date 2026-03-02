package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.dto.internal.OrderDetails;
import com.example.pvpeev.electronics_store.order.dto.internal.OrderRequestWithId;
import com.example.pvpeev.electronics_store.order.entity.OrderEntity;
import com.example.pvpeev.electronics_store.order.entity.OrderProductEntity;
import com.example.pvpeev.electronics_store.order.entity.OrderStatusEntity;
import com.example.pvpeev.electronics_store.order.mapper.OrderMapper;
import com.example.pvpeev.electronics_store.order.mapper.OrderProductMapper;
import com.example.pvpeev.electronics_store.order.repository.OrderProductRepository;
import com.example.pvpeev.electronics_store.order.repository.OrderRepository;
import com.example.pvpeev.electronics_store.order.repository.OrderStatusRepository;
import com.example.pvpeev.electronics_store.product.entity.ProductEntity;
import com.example.pvpeev.electronics_store.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.pvpeev.electronics_store.order.status.OrderStatus.ACCEPTED;

@Service
@RequiredArgsConstructor
public class OrderPersistenceService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderStatusRepository orderStatusRepository;

    private final OrderMapper orderMapper;
    private final OrderProductMapper orderProductMapper;

    public OrderDetails persistOrder(OrderRequestWithId request) {
        final OrderEntity entity = orderMapper.toNewEntity(request.getId(), request.getRequest());
        final OrderEntity order = orderRepository.save(entity);
        final List<OrderProductEntity> orderProducts = request.getRequest().getProducts().stream().map(op -> {
            final Optional<ProductEntity> product = productRepository.findById(op.getProductId());
            return orderProductMapper.toEntity(op, request.getId(), product.get().getPrice());
        }).toList();
        orderProductRepository.saveAll(orderProducts);
        orderStatusRepository.save(new OrderStatusEntity(null, order.getId(), ACCEPTED.getId()));
        return new OrderDetails(order.getId(), request.getRequest().getUserId(), 100, order.getShippingMethod(), entity.getPaymentType(), ACCEPTED.name());
    }
}
