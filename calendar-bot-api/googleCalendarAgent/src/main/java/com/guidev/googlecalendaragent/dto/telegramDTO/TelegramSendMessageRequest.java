package com.guidev.googlecalendaragent.dto.telegramDTO;

import com.fasterxml.jackson.annotation.JsonProperty;


public record TelegramSendMessageRequest (
        @JsonProperty("chat_id") long chatId,
        String text
) {
}
