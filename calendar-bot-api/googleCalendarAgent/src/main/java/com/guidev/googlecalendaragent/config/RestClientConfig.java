package com.guidev.googlecalendaragent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient agnoRestClient(
            RestClient.Builder builder,
            @Value("${agno.base-url}") String baseUrl
    ) {
        return builder
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Connection", "keep-alive")
                .requestFactory(new SimpleClientHttpRequestFactory())
                .build();
    }

    @Bean
    public RestClient telegramRestClient(
            RestClient.Builder builder,
            @Value("${telegram.api-base}") String apiBase,
            @Value("${telegram.bot-token}") String botToken
    ) {
        return builder
                .baseUrl(apiBase + "/bot" + botToken)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
