package com.guidev.googlecalendaragent.client;

import com.guidev.googlecalendaragent.dto.telegramDTO.TelegramGetResponse;
import com.guidev.googlecalendaragent.dto.telegramDTO.TelegramSendMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TelegramClient {

    private static final Logger log = LoggerFactory.getLogger(TelegramClient.class);

    private final RestClient restClient;

    public TelegramClient(@Qualifier("telegramRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public void sendMessage(long chatId, String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Telegram message text cannot be blank");
        }

        log.debug("Sending message to chatId={}", chatId);

        restClient.post()
                .uri("/sendMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new TelegramSendMessageRequest(chatId, text))
                .retrieve()
                .toBodilessEntity();
    }

    public TelegramGetResponse getUpdates(Long offset, int timeoutSeconds) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/getUpdates")
                            .queryParam("timeout", timeoutSeconds);
                    if (offset != null) {
                        uriBuilder.queryParam("offset", offset);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .body(TelegramGetResponse.class);
    }
}
