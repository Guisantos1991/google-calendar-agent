package com.guidev.googlecalendaragent.client;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.*;
import java.util.List;

@Component
public class GoogleCalendarClient {

    private static final Logger log = LoggerFactory.getLogger(GoogleCalendarClient.class);
    private static final String APPLICATION_NAME = "Telegram Calendar Bot (MVP)";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final List<String> SCOPES = List.of(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String CREDENTIALS_ENV_PATH = "GOOGLE_CREDENTIALS_PATH";

    private Calendar calendarService;

    private Calendar getCalendarService() {
        if (calendarService != null) return calendarService;

        try {
            var httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            InputStream in = loadCredentialsStream();

            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                    JSON_FACTORY,
                    new InputStreamReader(in)
            );

            var tokensDir = Path.of("tokens").toFile();
            var flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(tokensDir))
                    .setAccessType("offline")
                    .build();

            var receiver = new LocalServerReceiver.Builder().setPort(8888).setCallbackPath("/Callback").build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            calendarService = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            return calendarService;

        } catch (Exception e) {
            throw new RuntimeException("Falha ao inicializar Google Calendar client", e);
        }
    }

    private InputStream loadCredentialsStream() throws Exception {
        String credentialsPath = System.getenv(CREDENTIALS_ENV_PATH);
        if (credentialsPath != null && !credentialsPath.isBlank()) {
            return new FileInputStream(credentialsPath);
        }

        InputStream in = GoogleCalendarClient.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new IllegalStateException(
                    "Arquivo de credenciais não encontrado. Defina GOOGLE_CREDENTIALS_PATH ou crie src/main/resources/credentials.json a partir de credentials.example.json"
            );
        }

        return in;
    }

    public List<Event> listToday(String timezoneId) {
        ZoneId zone = ZoneId.of(timezoneId);
        LocalDate today = LocalDate.now(zone);
        return listBetween(today.atStartOfDay(zone).toInstant(), today.plusDays(1).atStartOfDay(zone).toInstant());
    }

    public List<Event> listTomorrow(String timezoneId) {
        ZoneId zone = ZoneId.of(timezoneId);
        LocalDate tomorrow = LocalDate.now(zone).plusDays(1);
        return listBetween(tomorrow.atStartOfDay(zone).toInstant(), tomorrow.plusDays(1).atStartOfDay(zone).toInstant());
    }

    /**
     * Retorna os próximos 10 compromissos a partir de agora (sem limite de data).
     */
    public List<Event> listNext(String timezoneId) {
        ZoneId zone = ZoneId.of(timezoneId);
        Instant now = Instant.now();
        Instant farFuture = LocalDate.now(zone).plusYears(1).atStartOfDay(zone).toInstant();
        return listBetween(now, farFuture);
    }

    /**
     * Lista eventos da semana atual (segunda a domingo da semana corrente).
     */
    public List<Event> listWeek(String timezoneId) {
        ZoneId zone = ZoneId.of(timezoneId);
        LocalDate today = LocalDate.now(zone);
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate nextMonday = monday.plusWeeks(1);
        return listBetween(monday.atStartOfDay(zone).toInstant(), nextMonday.atStartOfDay(zone).toInstant());
    }

    /**
     * Lista eventos da próxima semana (próxima segunda a próximo domingo).
     */
    public List<Event> listNextWeek(String timezoneId) {
        ZoneId zone = ZoneId.of(timezoneId);
        LocalDate today = LocalDate.now(zone);
        LocalDate nextMonday = today.with(DayOfWeek.MONDAY).plusWeeks(1);
        LocalDate nextNextMonday = nextMonday.plusWeeks(1);
        return listBetween(nextMonday.atStartOfDay(zone).toInstant(), nextNextMonday.atStartOfDay(zone).toInstant());
    }

    /**
     * Lista eventos do mês atual (dia 1 ao último dia do mês corrente).
     */
    public List<Event> listMonth(String timezoneId) {
        ZoneId zone = ZoneId.of(timezoneId);
        LocalDate today = LocalDate.now(zone);
        LocalDate firstDay = today.withDayOfMonth(1);
        LocalDate firstDayNextMonth = firstDay.plusMonths(1);
        return listBetween(firstDay.atStartOfDay(zone).toInstant(), firstDayNextMonth.atStartOfDay(zone).toInstant());
    }

    /**
     * Lista eventos do próximo mês (dia 1 ao último dia do próximo mês).
     */
    public List<Event> listNextMonth(String timezoneId) {
        ZoneId zone = ZoneId.of(timezoneId);
        LocalDate today = LocalDate.now(zone);
        LocalDate firstDayNextMonth = today.withDayOfMonth(1).plusMonths(1);
        LocalDate firstDayMonthAfter = firstDayNextMonth.plusMonths(1);
        return listBetween(firstDayNextMonth.atStartOfDay(zone).toInstant(), firstDayMonthAfter.atStartOfDay(zone).toInstant());
    }

    /**
     * Cria um novo evento no Google Calendar.
     *
     * @param summary   Título do evento
     * @param startTime Data/hora de início (ISO 8601: 2026-03-12T18:00:00)
     * @param endTime   Data/hora de fim (ISO 8601: 2026-03-12T19:00:00), pode ser null
     * @param timezone  Timezone do evento (ex: America/Sao_Paulo)
     * @return O evento criado
     */
    public Event createEvent(String summary, String startTime, String endTime, String timezone) {
        try {
            Calendar service = getCalendarService();
            ZoneId zone = ZoneId.of(timezone);

            Event event = new Event().setSummary(summary);

            // Parse e configura horário de início
            LocalDateTime startLocal = LocalDateTime.parse(startTime);
            ZonedDateTime startZoned = startLocal.atZone(zone);
            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(startZoned.toInstant().toEpochMilli()))
                    .setTimeZone(timezone);
            event.setStart(start);

            // Parse e configura horário de fim
            LocalDateTime endLocal;
            if (endTime != null && !endTime.isBlank()) {
                endLocal = LocalDateTime.parse(endTime);
            } else {
                // Padrão: 1 hora de duração
                endLocal = startLocal.plusHours(1);
            }
            ZonedDateTime endZoned = endLocal.atZone(zone);
            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(endZoned.toInstant().toEpochMilli()))
                    .setTimeZone(timezone);
            event.setEnd(end);

            Event createdEvent = service.events().insert("primary", event).execute();
            log.info("Evento criado: {} (ID: {})", createdEvent.getSummary(), createdEvent.getId());

            return createdEvent;
        } catch (Exception e) {
            log.error("Erro ao criar evento: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao criar evento no Google Calendar: " + e.getMessage(), e);
        }
    }

    /**
     * Cancela (deleta) um evento pelo ID.
     *
     * @param eventId ID do evento no Google Calendar
     */
    public void deleteEvent(String eventId) {
        try {
            Calendar service = getCalendarService();
            service.events().delete("primary", eventId).execute();
            log.info("Evento deletado: {}", eventId);
        } catch (Exception e) {
            log.error("Erro ao deletar evento: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao deletar evento no Google Calendar: " + e.getMessage(), e);
        }
    }

    /**
     * Busca um evento pelo título (summary) nos próximos 30 dias.
     *
     * @param summary  Título (ou parte) do evento
     * @param timezone Timezone para a busca
     * @return O primeiro evento encontrado, ou null se não encontrar
     */
    public Event findEventByName(String summary, String timezone) {
        try {
            Calendar service = getCalendarService();
            ZoneId zone = ZoneId.of(timezone);
            Instant now = Instant.now();
            Instant end = LocalDate.now(zone).plusDays(30).atStartOfDay(zone).toInstant();

            Events events = service.events().list("primary")
                    .setTimeMin(new com.google.api.client.util.DateTime(now.toEpochMilli()))
                    .setTimeMax(new com.google.api.client.util.DateTime(end.toEpochMilli()))
                    .setQ(summary)
                    .setSingleEvents(true)
                    .setOrderBy("startTime")
                    .setMaxResults(1)
                    .execute();

            List<Event> items = events.getItems();
            return (items != null && !items.isEmpty()) ? items.getFirst() : null;
        } catch (Exception e) {
            log.error("Erro ao buscar evento por nome: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar evento no Google Calendar: " + e.getMessage(), e);
        }
    }

    private static final int MAX_EVENTS = 10;

    private List<Event> listBetween(Instant start, Instant end) {
        try {
            Calendar service = getCalendarService();

            Events events = service.events().list("primary")
                    .setTimeMin(new com.google.api.client.util.DateTime(start.toEpochMilli()))
                    .setTimeMax(new com.google.api.client.util.DateTime(end.toEpochMilli()))
                    .setMaxResults(MAX_EVENTS)
                    .setSingleEvents(true)
                    .setOrderBy("startTime")
                    .execute();

            return events.getItems();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar eventos", e);
        }
    }
}
