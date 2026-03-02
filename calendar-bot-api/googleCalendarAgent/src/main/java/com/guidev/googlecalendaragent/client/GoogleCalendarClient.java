package com.guidev.googlecalendaragent.client;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
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
import com.google.api.services.calendar.model.Events;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.*;
import java.util.List;

@Component
public class GoogleCalendarClient {

    private static final String APPLICATION_NAME = "Telegram Calendar Bot (MVP)";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // Para MVP: só leitura
    private static final List<String> SCOPES = List.of(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private Calendar calendarService;

    private Calendar getCalendarService() {
        if (calendarService != null) return calendarService;

        try {
            var httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            InputStream in = GoogleCalendarClient.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
            if (in == null) throw new IllegalStateException("credentials.json não encontrado em src/main/resources");

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
            Credential credential = new AuthorizationCodeInstalledApp((AuthorizationCodeFlow) flow, receiver).authorize("user");

            calendarService = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            return calendarService;

        } catch (Exception e) {
            throw new RuntimeException("Falha ao inicializar Google Calendar client", e);
        }
    }

    public List<Event> listToday(String timezoneId) {
        ZoneId zone = ZoneId.of(timezoneId);
        LocalDate today = LocalDate.now(zone);

        ZonedDateTime start = today.atStartOfDay(zone);
        ZonedDateTime end = today.plusDays(1).atStartOfDay(zone);

        return listBetween(start.toInstant(), end.toInstant());
    }

    private List<Event> listBetween(Instant start, Instant end) {
        try {
            Calendar service = getCalendarService();

            Events events = service.events().list("primary")
                    .setTimeMin(new com.google.api.client.util.DateTime(start.toEpochMilli()))
                    .setTimeMax(new com.google.api.client.util.DateTime(end.toEpochMilli()))
                    .setSingleEvents(true)
                    .setOrderBy("startTime")
                    .execute();

            return events.getItems();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar eventos", e);
        }
    }

}
