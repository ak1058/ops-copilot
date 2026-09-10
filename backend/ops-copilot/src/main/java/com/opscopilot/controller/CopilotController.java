package com.opscopilot.controller;

import com.opscopilot.dto.CopilotRequest;
import com.opscopilot.dto.CopilotResponse;
import com.opscopilot.service.CopilotService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/copilot")
@CrossOrigin(origins = "*")
public class CopilotController {

    private final CopilotService copilotService;

    public CopilotController(CopilotService copilotService) {
        this.copilotService = copilotService;
    }

    @PostMapping("/query")
    public CopilotResponse query(@Valid @RequestBody CopilotRequest request) {
        return copilotService.processQuery(request);
    }
}
