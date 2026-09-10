package com.opscopilot.dto;

import com.opscopilot.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long orderId,
        PaymentStatus status,
        BigDecimal amount,
        String paymentMethod,
        String transactionId,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {
}
