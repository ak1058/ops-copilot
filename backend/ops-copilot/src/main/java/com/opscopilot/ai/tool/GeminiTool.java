package com.opscopilot.ai.tool;

import java.util.Map;

public interface GeminiTool {
    String getName();
    String getDescription();
    Map<String, Object> getParametersSchema();
    Object execute(Map<String, Object> arguments);
}
