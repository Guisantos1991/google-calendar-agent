package com.projetox.calendar.service;

import com.projetox.calendar.api.dto.CreateEventRequest;
import com.projetox.calendar.api.dto.EventResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalendarServiceTest {

    private CalendarService calendarService;

    @BeforeEach
    void setUp() {
        calendarService = new CalendarService();
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("deve criar evento e retornar resposta com ID gerado")
        void shouldCreateEventAndReturnResponseWithGeneratedId() {
            CreateEventRequest request = new CreateEventRequest(
                    "Reunião de Sprint",
                    "2026-02-20T10:00:00-03:00",
                    "2026-02-20T11:00:00-03:00",
                    "America/Sao_Paulo"
            );

            EventResponse response = calendarService.create(request);

            assertNotNull(response);
            assertNotNull(response.id());
            assertFalse(response.id().isBlank());
            assertEquals("Reunião de Sprint", response.title());
            assertEquals("2026-02-20T10:00:00-03:00", response.start());
            assertEquals("2026-02-20T11:00:00-03:00", response.end());
            assertEquals("America/Sao_Paulo", response.timezone());
        }

        @Test
        @DisplayName("deve gerar IDs únicos para cada evento")
        void shouldGenerateUniqueIds() {
            CreateEventRequest request = new CreateEventRequest(
                    "Evento",
                    "2026-02-20T10:00:00Z",
                    "2026-02-20T11:00:00Z",
                    "UTC"
            );

            EventResponse response1 = calendarService.create(request);
            EventResponse response2 = calendarService.create(request);

            assertNotEquals(response1.id(), response2.id());
        }
    }

    @Nested
    @DisplayName("list()")
    class ListEvents {

        @Test
        @DisplayName("deve retornar eventos dentro da janela de tempo")
        void shouldReturnEventsWithinTimeWindow() {
            calendarService.create(new CreateEventRequest(
                    "Dentro da janela",
                    "2026-02-20T10:00:00Z",
                    "2026-02-20T11:00:00Z",
                    "UTC"
            ));
            calendarService.create(new CreateEventRequest(
                    "Fora da janela",
                    "2026-03-01T10:00:00Z",
                    "2026-03-01T11:00:00Z",
                    "UTC"
            ));

            OffsetDateTime from = OffsetDateTime.of(2026, 2, 20, 0, 0, 0, 0, ZoneOffset.UTC);
            OffsetDateTime to = OffsetDateTime.of(2026, 2, 21, 0, 0, 0, 0, ZoneOffset.UTC);

            List<EventResponse> result = calendarService.list(from, to);

            assertEquals(1, result.size());
            assertEquals("Dentro da janela", result.get(0).title());
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há eventos no período")
        void shouldReturnEmptyListWhenNoEventsInPeriod() {
            calendarService.create(new CreateEventRequest(
                    "Evento futuro",
                    "2026-06-01T10:00:00Z",
                    "2026-06-01T11:00:00Z",
                    "UTC"
            ));

            OffsetDateTime from = OffsetDateTime.of(2026, 2, 1, 0, 0, 0, 0, ZoneOffset.UTC);
            OffsetDateTime to = OffsetDateTime.of(2026, 2, 28, 23, 59, 59, 0, ZoneOffset.UTC);

            List<EventResponse> result = calendarService.list(from, to);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("deve retornar eventos ordenados por data de início")
        void shouldReturnEventsSortedByStartDate() {
            calendarService.create(new CreateEventRequest(
                    "Evento B (mais tarde)",
                    "2026-02-20T15:00:00Z",
                    "2026-02-20T16:00:00Z",
                    "UTC"
            ));
            calendarService.create(new CreateEventRequest(
                    "Evento A (mais cedo)",
                    "2026-02-20T09:00:00Z",
                    "2026-02-20T10:00:00Z",
                    "UTC"
            ));
            calendarService.create(new CreateEventRequest(
                    "Evento C (meio)",
                    "2026-02-20T12:00:00Z",
                    "2026-02-20T13:00:00Z",
                    "UTC"
            ));

            OffsetDateTime from = OffsetDateTime.of(2026, 2, 20, 0, 0, 0, 0, ZoneOffset.UTC);
            OffsetDateTime to = OffsetDateTime.of(2026, 2, 21, 0, 0, 0, 0, ZoneOffset.UTC);

            List<EventResponse> result = calendarService.list(from, to);

            assertEquals(3, result.size());
            assertEquals("Evento A (mais cedo)", result.get(0).title());
            assertEquals("Evento C (meio)", result.get(1).title());
            assertEquals("Evento B (mais tarde)", result.get(2).title());
        }

        @Test
        @DisplayName("deve incluir evento cujo start é igual ao 'from' (inclusive)")
        void shouldIncludeEventWithStartEqualToFrom() {
            calendarService.create(new CreateEventRequest(
                    "Evento no limite",
                    "2026-02-20T00:00:00Z",
                    "2026-02-20T01:00:00Z",
                    "UTC"
            ));

            OffsetDateTime from = OffsetDateTime.of(2026, 2, 20, 0, 0, 0, 0, ZoneOffset.UTC);
            OffsetDateTime to = OffsetDateTime.of(2026, 2, 21, 0, 0, 0, 0, ZoneOffset.UTC);

            List<EventResponse> result = calendarService.list(from, to);

            assertEquals(1, result.size());
            assertEquals("Evento no limite", result.get(0).title());
        }

        @Test
        @DisplayName("deve excluir evento cujo start é igual ao 'to' (exclusive)")
        void shouldExcludeEventWithStartEqualToTo() {
            calendarService.create(new CreateEventRequest(
                    "Evento no limite superior",
                    "2026-02-21T00:00:00Z",
                    "2026-02-21T01:00:00Z",
                    "UTC"
            ));

            OffsetDateTime from = OffsetDateTime.of(2026, 2, 20, 0, 0, 0, 0, ZoneOffset.UTC);
            OffsetDateTime to = OffsetDateTime.of(2026, 2, 21, 0, 0, 0, 0, ZoneOffset.UTC);

            List<EventResponse> result = calendarService.list(from, to);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("deve retornar true ao deletar evento existente")
        void shouldReturnTrueWhenDeletingExistingEvent() {
            EventResponse created = calendarService.create(new CreateEventRequest(
                    "Evento para deletar",
                    "2026-02-20T10:00:00Z",
                    "2026-02-20T11:00:00Z",
                    "UTC"
            ));

            boolean result = calendarService.delete(created.id());

            assertTrue(result);
        }

        @Test
        @DisplayName("deve retornar false ao tentar deletar evento inexistente")
        void shouldReturnFalseWhenDeletingNonExistingEvent() {
            boolean result = calendarService.delete("id-inexistente");

            assertFalse(result);
        }

        @Test
        @DisplayName("evento deletado não deve aparecer na listagem")
        void deletedEventShouldNotAppearInList() {
            EventResponse created = calendarService.create(new CreateEventRequest(
                    "Evento temporário",
                    "2026-02-20T10:00:00Z",
                    "2026-02-20T11:00:00Z",
                    "UTC"
            ));

            calendarService.delete(created.id());

            OffsetDateTime from = OffsetDateTime.of(2026, 2, 20, 0, 0, 0, 0, ZoneOffset.UTC);
            OffsetDateTime to = OffsetDateTime.of(2026, 2, 21, 0, 0, 0, 0, ZoneOffset.UTC);

            List<EventResponse> result = calendarService.list(from, to);

            assertTrue(result.isEmpty());
        }
    }
}

