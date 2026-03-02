package com.guidev.googlecalendaragent.dto.telegramDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TelegramGetResponse(
        @JsonProperty("ok") boolean ok,
        @JsonProperty("result") List<TelegramUpdate> result
) {
}
