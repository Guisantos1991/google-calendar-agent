package com.guidev.googlecalendaragent.dto.telegramDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guidev.googlecalendaragent.dto.messageDTO.Message;

public record TelegramUpdate(
        @JsonProperty("update_id") Long updateId,
        @JsonProperty("message") Message message
) {
}
