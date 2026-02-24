package com.guidev.googlecalendaragent.client;

import com.guidev.googlecalendaragent.dto.telegramDTO.TelegramGetResponse;
import com.guidev.googlecalendaragent.dto.telegramDTO.TelegramSendMessageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Optional;

@Component
public class TelegramClient {
    private final RestClient restClient;
    private final String apiBase;
    private final String botToken;

    public TelegramClient(
            @Value("${telegram.api-base}") String apiBase,
            @Value("${telegram.bot-token}") String botToken
    ) {
        this.restClient = RestClient.create();
        this.apiBase = apiBase;
        this.botToken = botToken;
    }


    public void sendMessage(long chatId, String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Telegram message text cannot be blank");
        }

        String url = apiBase + "/bot" + botToken + "/sendMessage";

        restClient.post()
                .uri(url)
                .body(new TelegramSendMessageRequest(chatId, text))
                .retrieve()
                .toBodilessEntity();
    }

    public TelegramGetResponse getUpdates(Long offset, int timeoutSeconds) {
        String url = apiBase + "/bot" + botToken + "/getUpdates";

        String finalUrl = UriComponentsBuilder.fromUriString(url)
                .queryParam("timeout", timeoutSeconds)
                .queryParamIfPresent("offset", Optional.ofNullable(offset))
                .toUriString();

        return restClient.get()
                .uri(finalUrl)
                .retrieve()
                .body(TelegramGetResponse.class);
    }

}
