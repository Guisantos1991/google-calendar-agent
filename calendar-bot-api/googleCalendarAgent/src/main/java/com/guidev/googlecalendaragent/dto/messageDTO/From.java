package com.guidev.googlecalendaragent.dto.messageDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record From(
        @JsonProperty("id") Long id,
        @JsonProperty("username") String username,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName
) {
}
