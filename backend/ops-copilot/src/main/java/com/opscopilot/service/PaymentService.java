package com.opscopilot.service;

import com.opscopilot.dto.PaymentResponse;
import com.opscopilot.entity.Payment;
import com.opscopilot.exception.ResourceNotFoundException;
import com.opscopilot.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for order " + orderId + " does not exist"));

        return new PaymentResponse(
                payment.getOrderId(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getTransactionId(),
                payment.getPaidAt(),
                payment.getCreatedAt()
        );
    }
}
