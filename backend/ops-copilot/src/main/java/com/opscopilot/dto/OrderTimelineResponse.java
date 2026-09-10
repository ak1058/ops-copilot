package com.opscopilot.dto;

import java.util.List;

public record OrderTimelineResponse(
        Long orderId,
        List<OrderEventResponse> events
) {
}
