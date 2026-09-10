package com.opscopilot.ai.tool;

import com.opscopilot.service.CustomerService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GetCustomerTool implements GeminiTool {

    private final CustomerService customerService;

    public GetCustomerTool(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public String getName() {
        return "get_customer";
    }

    @Override
    public String getDescription() {
        return "Retrieve customer information using the customerId.";
    }

    @Override
    public Map<String, Object> getParametersSchema() {
        return Map.of(
            "type", "object",
            "properties", Map.of(
                "customerId", Map.of("type", "integer", "description", "The ID of the customer")
            ),
            "required", java.util.List.of("customerId")
        );
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        if (!arguments.containsKey("customerId")) {
            throw new IllegalArgumentException("Missing customerId argument");
        }
        Long customerId = Long.valueOf(arguments.get("customerId").toString());
        return customerService.getCustomer(customerId);
    }
}
