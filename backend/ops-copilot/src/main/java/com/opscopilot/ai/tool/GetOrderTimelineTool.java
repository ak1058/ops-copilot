package com.opscopilot.ai.tool;

import com.opscopilot.service.OrderTimelineService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GetOrderTimelineTool implements GeminiTool {

    private final OrderTimelineService orderTimelineService;

    public GetOrderTimelineTool(OrderTimelineService orderTimelineService) {
        this.orderTimelineService = orderTimelineService;
    }

    @Override
    public String getName() {
        return "get_order_timeline";
    }

    @Override
    public String getDescription() {
        return "Retrieve chronological events/timeline for an order.";
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
        return orderTimelineService.getOrderTimeline(orderId);
    }
}
