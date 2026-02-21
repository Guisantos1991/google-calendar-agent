package com.projetox.calendar.domain;

public record CalendarEvent(
        String id,
        String title,
        String start,
        String end,
        String timezone
) {
}
