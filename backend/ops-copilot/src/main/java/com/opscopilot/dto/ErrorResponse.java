package com.opscopilot.dto;

public record ErrorResponse(
        String error,
        String message,
        String requestId
) {
}
