package com.opscopilot.ai.tool;

import com.opscopilot.service.DeliveryService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GetDeliveryTool implements GeminiTool {

    private final DeliveryService deliveryService;

    public GetDeliveryTool(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Override
    public String getName() {
        return "get_delivery";
    }

    @Override
    public String getDescription() {
        return "Retrieve delivery information for an order.";
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
        return deliveryService.getDeliveryByOrderId(orderId);
    }
}
