package com.guidev.googlecalendaragent.client;

import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.guidev.googlecalendaragent.dto.agentDTO.AgentRequest;
import com.guidev.googlecalendaragent.dto.agentDTO.AgentResponse;


@Component
public class AgnoClient {

    private static final Logger log = LoggerFactory.getLogger(AgnoClient.class);

    private final RestClient restClient;


    public AgnoClient(@Qualifier("agnoRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public AgentResponse interpret(String userId, String timezone, String message) {
        var request = new AgentRequest(
                userId,
                timezone,
                OffsetDateTime.now().toString(),
                message
        );

        log.debug("Sending request to Agno: userId={}, timezone={}, message={}", userId, timezone, message);

        return restClient.post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(AgentResponse.class);

    }
}
