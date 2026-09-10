package com.opscopilot.ai.tool;

import com.opscopilot.service.PaymentService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GetPaymentTool implements GeminiTool {

    private final PaymentService paymentService;

    public GetPaymentTool(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public String getName() {
        return "get_payment";
    }

    @Override
    public String getDescription() {
        return "Retrieve payment information for an order.";
    }

    @Override
    public Map<String, Object> getParametersSchema() {
        return Map.of(
            "type", "object",
            "properties", Map.of(
                "orderId", Map.of("type", "integer", "description", "The ID of the order")
            ),
            "required", java.util.List.of("orderId")
        );
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        if (!arguments.containsKey("orderId")) {
            throw new IllegalArgumentException("Missing orderId argument");
        }
        Long orderId = Long.valueOf(arguments.get("orderId").toString());
        return paymentService.getPaymentByOrderId(orderId);
    }
}
