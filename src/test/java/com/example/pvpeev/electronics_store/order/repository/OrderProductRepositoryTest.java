package com.example.pvpeev.electronics_store.order.repository;

import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import com.example.pvpeev.electronics_store.order.entity.OrderEntity;
import com.example.pvpeev.electronics_store.order.entity.OrderProductEntity;
import com.example.pvpeev.electronics_store.order.payment.PaymentType;
import com.example.pvpeev.electronics_store.product.entity.ProductBrandEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductEntity;
import com.example.pvpeev.electronics_store.product.repository.ProductBrandRepository;
import com.example.pvpeev.electronics_store.product.repository.ProductCategoryRepository;
import com.example.pvpeev.electronics_store.product.repository.ProductRepository;
import com.example.pvpeev.electronics_store.repository.BaseRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static com.example.pvpeev.electronics_store.order.shipping.ShippingMethod.DHL;
import static org.assertj.core.api.Assertions.*;

class OrderProductRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ProductBrandRepository productBrandRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private ProductBrandEntity productBrand;
    private ProductCategoryEntity productCategory;
    private UserEntity user;

    @BeforeEach
    public void beforeEachTest() {
        productBrand = productBrandRepository.save(new ProductBrandEntity(null, "Apple"));
        productCategory = productCategoryRepository.save(new ProductCategoryEntity(null, "smartphones", "Smartphones", "Smartphones"));
        user = userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true));
    }

    @Test
    public void testAddingUnexistingProductToOrderThrowsException() {
        final OrderEntity orderEntity = new OrderEntity(UUID.randomUUID(), user.getId(), "Test address", PaymentType.DEBIT_CARD_VISA.getId(), "00000000", null, DHL.getId()).setNew();
        final UUID orderId = orderRepository.save(orderEntity).getId();
        final RuntimeException exception = catchRuntimeException(() -> orderProductRepository.save(new OrderProductEntity(null, orderId, UUID.randomUUID(), 10, 1000)));

        assertThat(exception)
                .as("Unexisting products can't be added to orders")
                .isInstanceOf(DataIntegrityViolationException.class)
                .extracting(Throwable::getMessage)
                .matches(s -> s.contains("fk_product_id"));
    }

    @Test
    public void testAddingProductToUnexistingOrderThrowsException() {
        final UUID productId = productRepository.save(new ProductEntity(null, this.productCategory.getId(), this.productBrand.getId(), "Iphone 17", "Iphone 17", 1000, 10, "http://fake-url.local", false)).getId();
        final RuntimeException exception = catchRuntimeException(() -> orderProductRepository.save(new OrderProductEntity(null, UUID.randomUUID(), productId, 10, 1000)));

        assertThat(exception)
                .as("Products can't be added to unexisting orders")
                .isInstanceOf(DataIntegrityViolationException.class)
                .extracting(Throwable::getMessage)
                .matches(s -> s.contains("fk_order_id"));
    }

    @Test
    public void testAddingProductToOrder() {
        final UUID productId = productRepository.save(new ProductEntity(null, this.productCategory.getId(), this.productBrand.getId(), "Iphone 17", "Iphone 17", 1000, 10, "http://fake-url.local", false)).getId();
        final OrderEntity orderEntity = new OrderEntity(UUID.randomUUID(), user.getId(), "Test address", PaymentType.DEBIT_CARD_VISA.getId(), "00000000", null, DHL.getId()).setNew();
        final UUID orderId = orderRepository.save(orderEntity).getId();
        assertThatNoException().isThrownBy(() -> orderProductRepository.save(new OrderProductEntity(null, orderId, productId, 10, 1000)));
    }

    @Test
    public void testGetTotalPrice() {
        final UUID productId = productRepository.save(new ProductEntity(null, this.productCategory.getId(), this.productBrand.getId(), "Iphone 17", "Iphone 17", 1000, 10, "http://fake-url.local", false)).getId();
        final OrderEntity orderEntity = new OrderEntity(UUID.randomUUID(), user.getId(), "Test address", PaymentType.DEBIT_CARD_VISA.getId(), "00000000", null, DHL.getId()).setNew();
        final UUID orderId = orderRepository.save(orderEntity).getId();
        orderProductRepository.save(new OrderProductEntity(null, orderId, productId, 10, 1000));
        assertThat(orderProductRepository.getTotalOrderPrice(orderId))
                .as("Only one product with price of 1000 is purchased")
                .isEqualTo(1000);
    }

}