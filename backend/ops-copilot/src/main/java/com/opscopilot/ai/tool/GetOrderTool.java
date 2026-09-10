package com.opscopilot.ai.tool;

import com.opscopilot.service.OrderService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GetOrderTool implements GeminiTool {

    private final OrderService orderService;

    public GetOrderTool(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public String getName() {
        return "get_order";
    }

    @Override
    public String getDescription() {
        return "Retrieve order information using the orderId.";
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
        return orderService.getOrder(orderId);
    }
}
