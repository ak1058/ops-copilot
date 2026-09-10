package com.opscopilot.ai;

import java.util.Map;

public record LLMResponse(
        String text,
        String toolCallId,
        String toolName,
        Map<String, Object> toolArguments,
        boolean isToolCall
) {
}
