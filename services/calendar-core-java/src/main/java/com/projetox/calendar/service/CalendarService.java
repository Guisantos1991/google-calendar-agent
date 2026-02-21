package com.projetox.calendar.service;

import com.projetox.calendar.api.dto.CreateEventRequest;
import com.projetox.calendar.api.dto.EventResponse;
import com.projetox.calendar.domain.CalendarEvent;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CalendarService {

    private final Map<String, CalendarEvent> events = new ConcurrentHashMap<>();

    public EventResponse create(CreateEventRequest request) {
        String id = UUID.randomUUID().toString();
        CalendarEvent event = new CalendarEvent(
                id,
                request.title(),
                request.start(),
                request.end(),
                request.timezone()
        );
        events.put(id, event);
        return toResponse(event);
    }

    public List<EventResponse> list(OffsetDateTime from, OffsetDateTime to) {
        List<EventResponse> result = new ArrayList<>();

        for (CalendarEvent event : events.values()) {
            OffsetDateTime start = OffsetDateTime.parse(event.start());
            boolean inWindow = (start.isEqual(from) || start.isAfter(from)) && start.isBefore(to);
            if (inWindow) {
                result.add(toResponse(event));
            }
        }

        result.sort(Comparator.comparing(EventResponse::start));
        return result;
    }

    public boolean delete(String id) {
        return events.remove(id) != null;
    }

    private EventResponse toResponse(CalendarEvent event) {
        return new EventResponse(
                event.id(),
                event.title(),
                event.start(),
                event.end(),
                event.timezone()
        );
    }
}
