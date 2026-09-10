package com.opscopilot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CopilotRequest(
        @NotBlank(message = "Query cannot be blank")
        @Size(max = 1000, message = "Query cannot exceed 1000 characters")
        String query
) {
}
