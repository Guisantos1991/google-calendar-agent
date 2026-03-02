package com.guidev.googlecalendaragent.dto.messageDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Chat(
        @JsonProperty("id") long id,
        @JsonProperty("type") String type
) {
}
