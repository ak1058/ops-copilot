package com.opscopilot.service;

import com.opscopilot.dto.DeliveryResponse;
import com.opscopilot.entity.Delivery;
import com.opscopilot.exception.ResourceNotFoundException;
import com.opscopilot.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery for order " + orderId + " does not exist"));

        return new DeliveryResponse(
                delivery.getOrderId(),
                delivery.getStatus(),
                delivery.getDeliveryPartner(),
                delivery.getTrackingNumber(),
                delivery.getExpectedDeliveryDate(),
                delivery.getAssignedAt(),
                delivery.getDeliveredAt()
        );
    }
}
