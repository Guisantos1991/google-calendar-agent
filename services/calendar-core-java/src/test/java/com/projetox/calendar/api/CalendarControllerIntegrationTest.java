package com.projetox.calendar.api;

import com.projetox.calendar.api.dto.CreateEventRequest;
import com.projetox.calendar.api.dto.EventResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CalendarControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private EventResponse createdEvent;

    @BeforeEach
    void setUp() {
        CreateEventRequest request = new CreateEventRequest(
                "Evento de Teste",
                "2026-02-20T10:00:00Z",
                "2026-02-20T11:00:00Z",
                "UTC"
        );
        ResponseEntity<EventResponse> response = restTemplate.postForEntity(
                "/calendar/events", request, EventResponse.class
        );
        createdEvent = response.getBody();
    }

    @Nested
    @DisplayName("POST /calendar/events")
    class PostEvent {

        @Test
        @DisplayName("deve criar evento e retornar 201 CREATED")
        void shouldCreateEventAndReturn201() {
            CreateEventRequest request = new CreateEventRequest(
                    "Novo Evento",
                    "2026-03-01T14:00:00Z",
                    "2026-03-01T15:00:00Z",
                    "UTC"
            );

            ResponseEntity<EventResponse> response = restTemplate.postForEntity(
                    "/calendar/events", request, EventResponse.class
            );

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().id());
            assertEquals("Novo Evento", response.getBody().title());
            assertEquals("2026-03-01T14:00:00Z", response.getBody().start());
            assertEquals("2026-03-01T15:00:00Z", response.getBody().end());
            assertEquals("UTC", response.getBody().timezone());
        }

        @Test
        @DisplayName("deve retornar 400 quando título está vazio")
        void shouldReturn400WhenTitleIsBlank() {
            CreateEventRequest request = new CreateEventRequest(
                    "",
                    "2026-03-01T14:00:00Z",
                    "2026-03-01T15:00:00Z",
                    "UTC"
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "/calendar/events", request, Map.class
            );

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("deve retornar 400 quando campos obrigatórios são nulos")
        void shouldReturn400WhenRequiredFieldsAreNull() {
            CreateEventRequest request = new CreateEventRequest(null, null, null, null);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "/calendar/events", request, Map.class
            );

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("GET /calendar/events")
    class GetEvents {

        @Test
        @DisplayName("deve retornar lista de eventos dentro do período")
        void shouldReturnEventsWithinPeriod() {
            ResponseEntity<List<EventResponse>> response = restTemplate.exchange(
                    "/calendar/events?from=2026-02-20T00:00:00Z&to=2026-02-21T00:00:00Z",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertFalse(response.getBody().isEmpty());
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há eventos no período")
        void shouldReturnEmptyListWhenNoEventsInPeriod() {
            ResponseEntity<List<EventResponse>> response = restTemplate.exchange(
                    "/calendar/events?from=2030-01-01T00:00:00Z&to=2030-01-02T00:00:00Z",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isEmpty());
        }
    }

    @Nested
    @DisplayName("DELETE /calendar/events/{id}")
    class DeleteEvent {

        @Test
        @DisplayName("deve retornar 204 ao deletar evento existente")
        void shouldReturn204WhenDeletingExistingEvent() {
            assertNotNull(createdEvent, "Evento criado no setUp não pode ser nulo");

            ResponseEntity<Void> response = restTemplate.exchange(
                    "/calendar/events/" + createdEvent.id(),
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        @DisplayName("deve retornar 404 ao tentar deletar evento inexistente")
        void shouldReturn404WhenDeletingNonExistingEvent() {
            ResponseEntity<Void> response = restTemplate.exchange(
                    "/calendar/events/id-que-nao-existe",
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }
}

