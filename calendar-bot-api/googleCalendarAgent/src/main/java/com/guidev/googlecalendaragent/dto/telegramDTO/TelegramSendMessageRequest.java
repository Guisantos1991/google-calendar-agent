package com.guidev.googlecalendaragent.dto.telegramDTO;

import com.fasterxml.jackson.annotation.JsonProperty;


public record TelegramSendMessageRequest (
        @JsonProperty("chat_id") long chatId,
        @JsonProperty("text") String text,
        @JsonProperty("parse_mode") String parseMode
) {
    public TelegramSendMessageRequest(long chatId, String text) {
        this(chatId, text, "HTML");
    }
}
