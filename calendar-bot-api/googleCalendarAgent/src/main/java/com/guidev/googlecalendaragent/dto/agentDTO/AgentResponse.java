package com.guidev.googlecalendaragent.dto.agentDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record AgentResponse(
        @JsonProperty("action") String action,
        @JsonProperty("confidence") double confidence,
        @JsonProperty("args") Map<String, Object> args
) {
}
