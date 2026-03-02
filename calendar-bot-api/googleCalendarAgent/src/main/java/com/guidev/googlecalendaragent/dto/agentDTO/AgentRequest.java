package com.guidev.googlecalendaragent.dto.agentDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AgentRequest(
        @JsonProperty("user_id") String userId,
        @JsonProperty("timezone") String timezone,
        @JsonProperty("now") String now,
        @JsonProperty("message") String message
) {
}
