package com.opscopilot.service;

import com.opscopilot.dto.OrderResponse;
import com.opscopilot.entity.Order;
import com.opscopilot.entity.OrderStatus;
import com.opscopilot.exception.ResourceNotFoundException;
import com.opscopilot.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository);
    }

    @Test
    void getOrder_ShouldReturnOrderResponse_WhenOrderExists() {
        Order order = new Order();
        order.setId(1289L);
        order.setCustomerId(101L);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(new BigDecimal("1499.00"));
        order.setCurrency("INR");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        when(orderRepository.findById(1289L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrder(1289L);

        assertNotNull(response);
        assertEquals(1289L, response.orderId());
        assertEquals(101L, response.customerId());
        assertEquals(OrderStatus.CONFIRMED, response.status());
    }

    @Test
    void getOrder_ShouldThrowException_WhenOrderDoesNotExist() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrder(999L));
    }
}
