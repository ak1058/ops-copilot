package com.opscopilot.ai;

import java.util.List;
import java.util.Map;

public interface LLMClient {
    LLMResponse execute(String systemInstruction, List<Map<String, Object>> chatHistory, List<Map<String, Object>> toolDeclarations);
}
