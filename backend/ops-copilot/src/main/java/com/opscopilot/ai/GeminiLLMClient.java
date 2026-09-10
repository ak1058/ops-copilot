package com.opscopilot.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.opscopilot.exception.GeminiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GeminiLLMClient implements LLMClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiLLMClient.class);

    private final String apiKey;
    private final String model;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiLLMClient(
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model:gemini-1.5-pro-latest}") String model,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.model = model;
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    @Override
    public LLMResponse execute(String systemInstruction, List<Map<String, Object>> chatHistory, List<Map<String, Object>> toolDeclarations) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new GeminiException("GEMINI_API_KEY is not configured.");
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        try {
            ObjectNode requestBody = objectMapper.createObjectNode();

            // System Instruction
            ObjectNode sysInst = requestBody.putObject("systemInstruction");
            ArrayNode sysParts = sysInst.putArray("parts");
            sysParts.addObject().put("text", systemInstruction);

            // Contents (Chat History)
            ArrayNode contentsNode = requestBody.putArray("contents");
            for (Map<String, Object> turn : chatHistory) {
                contentsNode.add(objectMapper.valueToTree(turn));
            }

            // Tools
            if (toolDeclarations != null && !toolDeclarations.isEmpty()) {
                ArrayNode toolsNode = requestBody.putArray("tools");
                ObjectNode functionDeclarationsNode = toolsNode.addObject();
                ArrayNode funcs = functionDeclarationsNode.putArray("functionDeclarations");
                for (Map<String, Object> t : toolDeclarations) {
                    funcs.add(objectMapper.valueToTree(t));
                }
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            log.info("Sending request to Gemini model: {}", model);
            String responseStr = restTemplate.postForObject(url, request, String.class);
            JsonNode root = objectMapper.readTree(responseStr);

            JsonNode candidates = root.path("candidates");
            if (candidates.isMissingNode() || !candidates.isArray() || candidates.isEmpty()) {
                throw new GeminiException("Invalid response from Gemini API: Missing candidates");
            }

            JsonNode firstCandidate = candidates.get(0);
            JsonNode content = firstCandidate.path("content");
            JsonNode parts = content.path("parts");

            if (parts.isArray() && !parts.isEmpty()) {
                JsonNode firstPart = parts.get(0);
                
                if (firstPart.has("functionCall")) {
                    JsonNode functionCall = firstPart.get("functionCall");
                    String toolName = functionCall.get("name").asText();
                    JsonNode argsNode = functionCall.get("args");
                    
                    Map<String, Object> args = new HashMap<>();
                    if (argsNode != null && argsNode.isObject()) {
                        argsNode.fields().forEachRemaining(entry -> {
                            if (entry.getValue().isNumber()) {
                                args.put(entry.getKey(), entry.getValue().asLong());
                            } else {
                                args.put(entry.getKey(), entry.getValue().asText());
                            }
                        });
                    }
                    return new LLMResponse(null, toolName, toolName, args, true);
                } else if (firstPart.has("text")) {
                    return new LLMResponse(firstPart.get("text").asText(), null, null, null, false);
                }
            }
            
            throw new GeminiException("Unexpected response structure from Gemini API");

        } catch (GeminiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to execute Gemini API request", e);
            throw new GeminiException("Failed to execute Gemini API request: " + e.getMessage(), e);
        }
    }
}
