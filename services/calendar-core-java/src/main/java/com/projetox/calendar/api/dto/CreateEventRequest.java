package com.projetox.calendar.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateEventRequest(
        @NotBlank String title,
        @NotBlank String start,
        @NotBlank String end,
        @NotBlank String timezone
) {
}
