package com.guidev.googlecalendaragent.dto.messageDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Message(
        @JsonProperty("message_id") long messageId,
        @JsonProperty("chat") Chat chat,
        @JsonProperty("from") From from,
        @JsonProperty("text") String text
) {
}
