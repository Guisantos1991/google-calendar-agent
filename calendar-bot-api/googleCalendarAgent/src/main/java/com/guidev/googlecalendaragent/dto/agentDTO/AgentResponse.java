package com.guidev.googlecalendaragent.dto.agentDTO;

import java.util.Map;

public record AgentResponse(
        String action,
        double confidence,
        Map<String, Object> args
) {
}
