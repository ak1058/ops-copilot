package com.opscopilot.dto;

import com.opscopilot.entity.EventType;
import java.time.LocalDateTime;

public record OrderEventResponse(
        EventType eventType,
        String description,
        LocalDateTime createdAt
) {
}
