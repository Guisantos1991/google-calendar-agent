package com.guidev.googlecalendaragent.client;

import com.guidev.googlecalendaragent.dto.agentDTO.AgentRequest;
import com.guidev.googlecalendaragent.dto.agentDTO.AgentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;

@Component
public class AgnoClient {

    private final RestClient restClient;

    public AgnoClient(RestClient.Builder builder,
                      @Value("${agno.base-url:http://localhost:8000}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public AgentResponse interpret(String userId, String timezone, String message) {
        AgentRequest req = new AgentRequest(
                userId,
                timezone,
                message
        );

        return restClient.post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .body(AgentResponse.class);
    }
}
