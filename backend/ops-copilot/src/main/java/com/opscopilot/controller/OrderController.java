package com.opscopilot.controller;

import com.opscopilot.dto.DeliveryResponse;
import com.opscopilot.dto.OrderResponse;
import com.opscopilot.dto.OrderTimelineResponse;
import com.opscopilot.dto.PaymentResponse;
import com.opscopilot.service.DeliveryService;
import com.opscopilot.service.OrderService;
import com.opscopilot.service.OrderTimelineService;
import com.opscopilot.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final DeliveryService deliveryService;
    private final OrderTimelineService orderTimelineService;

    public OrderController(OrderService orderService, PaymentService paymentService, DeliveryService deliveryService, OrderTimelineService orderTimelineService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.deliveryService = deliveryService;
        this.orderTimelineService = orderTimelineService;
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping("/{orderId}/payment")
    public PaymentResponse getPayment(@PathVariable Long orderId) {
        return paymentService.getPaymentByOrderId(orderId);
    }

    @GetMapping("/{orderId}/delivery")
    public DeliveryResponse getDelivery(@PathVariable Long orderId) {
        return deliveryService.getDeliveryByOrderId(orderId);
    }

    @GetMapping("/{orderId}/timeline")
    public OrderTimelineResponse getTimeline(@PathVariable Long orderId) {
        return orderTimelineService.getOrderTimeline(orderId);
    }
}
