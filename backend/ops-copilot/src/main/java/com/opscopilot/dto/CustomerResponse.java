package com.opscopilot.dto;

public record CustomerResponse(
        Long customerId,
        String name,
        String email,
        String phone
) {
}
