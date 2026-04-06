package com.example.pvpeev.electronics_store.order.status;

import com.example.pvpeev.electronics_store.order.dto.OrderProductRequest;
import com.example.pvpeev.electronics_store.order.dto.OrderRequest;
import com.example.pvpeev.electronics_store.order.dto.internal.OrderRequestWithId;
import com.example.pvpeev.electronics_store.order.entity.OrderEntity;
import com.example.pvpeev.electronics_store.order.entity.OrderProductEntity;
import com.example.pvpeev.electronics_store.order.mapper.OrderMapper;
import com.example.pvpeev.electronics_store.order.mapper.OrderProductMapper;
import com.example.pvpeev.electronics_store.order.pipeline.OrderStage;
import com.example.pvpeev.electronics_store.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.pvpeev.electronics_store.order.status.OrderStatus.ACCEPTED;

@Component
@RequiredArgsConstructor
public class AcceptedStatusKafkaListener {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductService productService;
    private final OrderMapper orderMapper;
    private final OrderProductMapper orderProductMapper;
    private final JdbcTemplate jdbcTemplate;

    @KafkaListener(topics = "#T{(com.example.pvpeev.electronics_store.order.pipeline.OrderStage).ACCEPTED.getStage()}",
            groupId = "accepting-group",
            batch = "true")
    @Transactional
    public void processPlacedOrders(List<OrderRequestWithId> orderRequests) {
        final Set<UUID> productIds = getProductIds(orderRequests);

        final Map<UUID, Integer> productPrices = productService.getProductPrices(productIds);
        final Map<UUID, Integer> productQuantities = productService.getProductQuantities(productIds);


        final List<OrderRequestWithId> invalidOrders = new ArrayList<>();
        final List<OrderRequestWithId> canceledOrders = new ArrayList<>();
        final List<OrderEntity> validOrders = new ArrayList<>();
        final List<OrderProductEntity> validOrderProducts = new ArrayList<>();

        separateOrders(orderRequests, productPrices, productQuantities, invalidOrders, canceledOrders, validOrders, validOrderProducts);

        if (!invalidOrders.isEmpty()) {
            invalidOrders.forEach(or -> kafkaTemplate.send(OrderStage.INVALID.getStage(), or));
        }
        if (!canceledOrders.isEmpty()) {
            canceledOrders.forEach(or -> kafkaTemplate.send(OrderStage.CANCELED.getStage(), or));
        }

        if (validOrders.isEmpty()) {
            return;
        }
        persistOrders(validOrders);

        persistOrderProducts(validOrderProducts);
        persistOrderStatuses(validOrders);

        validOrders.forEach(or -> kafkaTemplate.send(OrderStage.PROCESS_PAYMENT.getStage(), or.getId()));
    }

    private void persistOrderStatuses(List<OrderEntity> validOrders) {
        jdbcTemplate.batchUpdate("""
                        INSERT INTO order_status (order_id, order_status)
                        VALUES(?,?)
                        """,
                validOrders,
                validOrders.size(),
                (ps, or) -> {
                    ps.setObject(1, or.getId());
                    ps.setInt(2, ACCEPTED.getId());
                });
    }

    private void persistOrderProducts(List<OrderProductEntity> validOrderProducts) {
        jdbcTemplate.batchUpdate("""
                        INSERT INTO order_product (order_id, product_id, quantity, price_at_purchase)
                        VALUES(?, ?, ?, ?)
                        """,
                validOrderProducts,
                validOrderProducts.size(),
                (ps, op) -> {
                    ps.setObject(1, op.getOrderId());
                    ps.setObject(2, op.getProductId());
                    ps.setInt(3, op.getQuantity());
                    ps.setInt(4, op.getPriceAtPurchase());
                });
    }

    private void persistOrders(List<OrderEntity> validOrders) {
        jdbcTemplate.batchUpdate("""
                        INSERT INTO public.order (id, user_id, order_address, payment_type, phone_number, tracking_code, shipping_method)
                        VALUES(?, ?, ?, ?, ?, ?, ?)
                        """,
                validOrders,
                validOrders.size(),
                (ps, or) -> {
                    ps.setObject(1, or.getId());
                    ps.setObject(2, or.getUserId());
                    ps.setString(3, or.getOrderAddress());
                    ps.setInt(4, or.getPaymentType());
                    ps.setString(5, or.getPhoneNumber());
                    ps.setString(6, or.getTrackingCode());
                    ps.setInt(7, or.getShippingMethod());
                });
    }

    private void separateOrders(List<OrderRequestWithId> orderRequests, Map<UUID, Integer> productPrices, Map<UUID, Integer> productQuantities, List<OrderRequestWithId> invalidOrders, List<OrderRequestWithId> canceledOrders, List<OrderEntity> validOrders, List<OrderProductEntity> validOrderProducts) {
        orderRequests.stream()
                .sorted(Comparator.comparing(OrderRequestWithId::getDate))
                .forEach(orderRequest -> {
                    List<OrderProductRequest> productRequests = orderRequest.getRequest().getProducts();
                    List<OrderProductEntity> currentOrderProducts = new ArrayList<>();
                    boolean isOrderValid = true;

                    for (OrderProductRequest productReq : productRequests) {
                        Integer price = productPrices.get(productReq.getProductId());
                        Integer stock = productQuantities.get(productReq.getProductId());

                        if (price == null) {
                            invalidOrders.add(orderRequest);
                            isOrderValid = false;
                            break;
                        }
                        if (stock == null || stock - productReq.getQuantity() < 0) {
                            canceledOrders.add(orderRequest);
                            isOrderValid = false;
                            break;
                        }

                        productQuantities.put(productReq.getProductId(), stock - productReq.getQuantity());

                        currentOrderProducts.add(orderProductMapper.toEntity(productReq, orderRequest.getId(), price));
                    }

                    if (isOrderValid) {
                        validOrders.add(orderMapper.toNewEntity(orderRequest.getId(), orderRequest.getRequest()));
                        validOrderProducts.addAll(currentOrderProducts);
                    }
                });
    }

    private static Set<UUID> getProductIds(List<OrderRequestWithId> orderRequests) {
        return orderRequests.stream()
                .map(OrderRequestWithId::getRequest)
                .map(OrderRequest::getProducts)
                .flatMap(Collection::stream)
                .map(OrderProductRequest::getProductId)
                .collect(Collectors.toUnmodifiableSet());
    }
}
