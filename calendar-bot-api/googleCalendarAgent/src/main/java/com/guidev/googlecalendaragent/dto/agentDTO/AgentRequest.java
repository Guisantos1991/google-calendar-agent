package com.guidev.googlecalendaragent.dto.agentDTO;

public record AgentRequest(
        String user_id,
        String timezone,
        String message
) {
}
