package com.opscopilot.ai;

import com.opscopilot.ai.tool.GeminiTool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ToolRegistry {

    private final Map<String, GeminiTool> tools;

    public ToolRegistry(List<GeminiTool> toolList) {
        this.tools = toolList.stream()
                .collect(Collectors.toMap(GeminiTool::getName, Function.identity()));
    }

    public GeminiTool getTool(String name) {
        return tools.get(name);
    }

    public List<GeminiTool> getAllTools() {
        return List.copyOf(tools.values());
    }
}
