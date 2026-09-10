package com.opscopilot.service;

import com.opscopilot.dto.OrderEventResponse;
import com.opscopilot.dto.OrderTimelineResponse;
import com.opscopilot.entity.OrderEvent;
import com.opscopilot.exception.ResourceNotFoundException;
import com.opscopilot.repository.OrderEventRepository;
import com.opscopilot.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderTimelineService {

    private final OrderEventRepository orderEventRepository;
    private final OrderRepository orderRepository;

    public OrderTimelineService(OrderEventRepository orderEventRepository, OrderRepository orderRepository) {
        this.orderEventRepository = orderEventRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public OrderTimelineResponse getOrderTimeline(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order " + orderId + " does not exist");
        }

        List<OrderEvent> events = orderEventRepository.findByOrderIdOrderByCreatedAtAsc(orderId);
        
        List<OrderEventResponse> eventResponses = events.stream()
                .map(e -> new OrderEventResponse(e.getEventType(), e.getDescription(), e.getCreatedAt()))
                .collect(Collectors.toList());

        return new OrderTimelineResponse(orderId, eventResponses);
    }
}
