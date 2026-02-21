package com.projetox.calendar.api.dto;

public record EventResponse(
        String id,
        String title,
        String start,
        String end,
        String timezone
) {
}
