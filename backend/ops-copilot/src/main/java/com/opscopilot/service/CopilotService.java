package com.opscopilot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opscopilot.ai.LLMClient;
import com.opscopilot.ai.LLMResponse;
import com.opscopilot.ai.ToolRegistry;
import com.opscopilot.ai.tool.GeminiTool;
import com.opscopilot.dto.CopilotRequest;
import com.opscopilot.dto.CopilotResponse;
import com.opscopilot.exception.GeminiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CopilotService {

    private static final Logger log = LoggerFactory.getLogger(CopilotService.class);

    private final LLMClient llmClient;
    private final ToolRegistry toolRegistry;
    private final ObjectMapper objectMapper;
    private final int maxIterations;

    public CopilotService(LLMClient llmClient, ToolRegistry toolRegistry, ObjectMapper objectMapper,
                          @Value("${gemini.max-tool-iterations:5}") int maxIterations) {
        this.llmClient = llmClient;
        this.toolRegistry = toolRegistry;
        this.objectMapper = objectMapper;
        this.maxIterations = maxIterations;
    }

    public CopilotResponse processQuery(CopilotRequest request) {
        String requestId = "req_" + UUID.randomUUID().toString().substring(0, 8);
        log.info("Starting requestId={} query='{}'", requestId, request.query());

        String systemInstruction = "You are an operations assistant for an e-commerce platform. " +
                "You help answer queries about orders, payments, deliveries, and customers. " +
                "Core rules: " +
                "1. Use tools for operational facts. " +
                "2. Never invent order, payment, delivery, or customer information. " +
                "3. If information is unavailable, explicitly state that it is unavailable. " +
                "4. Keep operational answers concise but useful. " +
                "5. Only return a final answer when you have sufficient tool data.";

        List<Map<String, Object>> chatHistory = new ArrayList<>();
        chatHistory.add(Map.of("role", "user", "parts", List.of(Map.of("text", request.query()))));

        List<Map<String, Object>> toolDeclarations = new ArrayList<>();
        for (GeminiTool tool : toolRegistry.getAllTools()) {
            Map<String, Object> declaration = new HashMap<>();
            declaration.put("name", tool.getName());
            declaration.put("description", tool.getDescription());
            if (tool.getParametersSchema() != null) {
                declaration.put("parameters", tool.getParametersSchema());
            }
            toolDeclarations.add(declaration);
        }

        List<String> toolsUsed = new ArrayList<>();
        List<String> evidence = new ArrayList<>();
        Long extractedOrderId = null;

        for (int i = 0; i < maxIterations; i++) {
            LLMResponse llmResponse = llmClient.execute(systemInstruction, chatHistory, toolDeclarations);

            if (!llmResponse.isToolCall()) {
                log.info("requestId={} finished. iterations={}, answer='{}'", requestId, i, llmResponse.text());
                return new CopilotResponse(
                        requestId,
                        llmResponse.text(),
                        "OPERATIONS_QUERY",
                        extractedOrderId,
                        toolsUsed,
                        evidence
                );
            }

            String toolName = llmResponse.toolName();
            Map<String, Object> args = llmResponse.toolArguments();
            
            log.info("requestId={} executing tool={}", requestId, toolName);
            toolsUsed.add(toolName);

            // Record the model's function call in history
            Map<String, Object> modelTurn = new HashMap<>();
            modelTurn.put("role", "model");
            modelTurn.put("parts", List.of(
                Map.of("functionCall", Map.of("name", toolName, "args", args))
            ));
            chatHistory.add(modelTurn);

            if (args.containsKey("orderId") && extractedOrderId == null) {
                extractedOrderId = Long.valueOf(args.get("orderId").toString());
            }

            GeminiTool tool = toolRegistry.getTool(toolName);
            Object result;
            if (tool == null) {
                result = Map.of("error", "Tool " + toolName + " not found");
            } else {
                try {
                    result = tool.execute(args);
                    evidence.add(toolName + " returned data for " + args);
                } catch (Exception e) {
                    log.error("requestId={} tool execution failed for {}", requestId, toolName, e);
                    result = Map.of("error", e.getMessage());
                }
            }

            // Return tool result to model
            Map<String, Object> functionResponse = new HashMap<>();
            functionResponse.put("name", toolName);
            functionResponse.put("response", result);

            Map<String, Object> userTurn = new HashMap<>();
            userTurn.put("role", "user");
            userTurn.put("parts", List.of(
                Map.of("functionResponse", functionResponse)
            ));
            chatHistory.add(userTurn);
        }

        throw new GeminiException("Exceeded maximum tool iterations (" + maxIterations + ") for requestId=" + requestId);
    }
}
