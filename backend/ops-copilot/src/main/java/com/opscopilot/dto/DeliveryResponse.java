package com.opscopilot.dto;

import com.opscopilot.entity.DeliveryStatus;
import java.time.LocalDateTime;

public record DeliveryResponse(
        Long orderId,
        DeliveryStatus status,
        String deliveryPartner,
        String trackingNumber,
        LocalDateTime expectedDeliveryDate,
        LocalDateTime assignedAt,
        LocalDateTime deliveredAt
) {
}
