package com.opscopilot.dto;

import java.util.List;

public record CopilotResponse(
        String requestId,
        String answer,
        String intent,
        Long orderId,
        List<String> toolsUsed,
        List<String> evidence
) {
}
