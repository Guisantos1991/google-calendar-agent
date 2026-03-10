package com.guidev.googlecalendaragent.service;

import com.google.api.services.calendar.model.Event;
import com.guidev.googlecalendaragent.dto.agentDTO.AgentResponse;
import com.guidev.googlecalendaragent.client.GoogleCalendarClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Responsável pela formatação de eventos do Google Calendar
 * em mensagens legíveis para o usuário.
 */
@Service
public class CalendarEventFormatter {

    private final String defaultTimezone;
    private final GoogleCalendarClient googleCalendarClient;

    public CalendarEventFormatter(
            GoogleCalendarClient googleCalendarClient,
            @Value("${app.default-timezone}") String defaultTimezone
    ) {
        this.googleCalendarClient = googleCalendarClient;
        this.defaultTimezone = defaultTimezone;
    }

    public String buildTodayEventsMessage() {
        var events = googleCalendarClient.listToday(defaultTimezone);
        return formatEventList("📅 Compromissos de hoje:\n", "Hoje tá limpo. Zero compromissos. 👌", events, false);
    }

    public String buildTomorrowEventsMessage() {
        var events = googleCalendarClient.listTomorrow(defaultTimezone);
        return formatEventList("📅 Compromissos de amanhã:\n", "Amanhã tá limpo. Zero compromissos. 👌", events, false);
    }

    public String buildNextEventsMessage() {
        var events = googleCalendarClient.listNext(defaultTimezone);
        return formatEventList("📅 Próximos compromissos:\n", "Não há próximos compromissos. 👌", events, true);
    }

    public String buildNextWeekEventsMessage() {
        var events = googleCalendarClient.listNextWeek(defaultTimezone);
        return formatEventList("📅 Compromissos da próxima semana:\n", "Não há compromissos para a próxima semana. 👌", events, true);
    }

    public String buildNextMonthEventsMessage() {
        var events = googleCalendarClient.listNextMonth(defaultTimezone);
        return formatEventList("📅 Compromissos do próximo mês:\n", "Não há compromissos para o próximo mês. 👌", events, true);
    }

    public String createEvent(AgentResponse response) {
        if (response.args() == null || response.args().isEmpty()) {
            return "Não recebi os detalhes para criar o evento. Pode repetir?";
        }

        try {
            String summary = (String) response.args().get("summary");
            String startStr = (String) response.args().get("start_time");
            String endStr = (String) response.args().get("end_time");

            if (summary == null || startStr == null) {
                return "Faltam informações (título ou horário) para criar o compromisso.";
            }

            // Cria o evento no Google Calendar
            googleCalendarClient.createEvent(summary, startStr, endStr, defaultTimezone);

            // Formata resposta amigável
            String formattedTime = formatEventTime(startStr, endStr);

            return "✅ Agendado: " + summary + " para " + formattedTime + ".";
        } catch (Exception e) {
            return "Erro ao criar evento: " + e.getMessage();
        }
    }

    public String cancelEvent(AgentResponse response) {
        if (response.args() == null || response.args().isEmpty()) {
            return "Não recebi os detalhes para cancelar o evento. Pode repetir?";
        }

        try {
            String eventId = response.args().get("event_id") != null
                    ? String.valueOf(response.args().get("event_id"))
                    : null;
            String summary = response.args().get("summary") != null
                    ? String.valueOf(response.args().get("summary"))
                    : null;

            if (eventId != null && !eventId.isBlank()) {
                googleCalendarClient.deleteEvent(eventId);
                return "❌ Evento cancelado com sucesso!";
            }

            if (summary != null && !summary.isBlank()) {
                var event = googleCalendarClient.findEventByName(summary, defaultTimezone);
                if (event == null) {
                    return "Não encontrei nenhum evento com o nome \"" + summary + "\" nos próximos 30 dias.";
                }
                googleCalendarClient.deleteEvent(event.getId());
                String title = event.getSummary() != null ? event.getSummary() : summary;
                return "❌ Evento cancelado: " + title;
            }

            return "Preciso do nome ou ID do evento para cancelar. Pode informar?";
        } catch (Exception e) {
            return "Erro ao cancelar evento: " + e.getMessage();
        }
    }

    private String formatEventTime(String startStr, String endStr) {
        try {
            var start = java.time.LocalDateTime.parse(startStr);
            String dateStr = start.toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM"));
            String startTime = start.toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

            if (endStr != null) {
                var end = java.time.LocalDateTime.parse(endStr);
                String endTime = end.toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                return dateStr + " das " + startTime + " às " + endTime;
            }
            return dateStr + " às " + startTime;
        } catch (Exception e) {
            return startStr;
        }
    }

    private static final int MAX_EVENTS = 10;

    private String formatEventList(String header, String emptyMsg, List<Event> events, boolean showDate) {
        if (events == null || events.isEmpty()) {
            return emptyMsg;
        }

        ZoneId zone = ZoneId.of(defaultTimezone);
        StringBuilder sb = new StringBuilder("<b>").append(header).append("</b>");

        for (var ev : events) {
            String title = (ev.getSummary() == null || ev.getSummary().isBlank()) ? "(sem título)" : ev.getSummary();
            String when;
            var start = ev.getStart();

            if (start != null && start.getDateTime() != null) {
                Instant inst = Instant.ofEpochMilli(start.getDateTime().getValue());
                ZonedDateTime zdt = ZonedDateTime.ofInstant(inst, zone);
                String hhmm = zdt.toLocalTime().toString();
                if (hhmm.length() >= 5) hhmm = hhmm.substring(0, 5);

                if (showDate) {
                    when = zdt.toLocalDate().toString() + " " + hhmm;
                } else {
                    when = hhmm;
                }
            } else if (start != null && start.getDate() != null) {
                when = showDate ? start.getDate().toString() : "Dia inteiro";
            } else {
                when = "Sem horário";
            }

            String where = ev.getLocation();
            String meet = ev.getHangoutLink();

            sb.append("• ").append(when).append(" — ").append(title);
            if (meet != null && !meet.isBlank()) sb.append(" (Meet)");
            else if (where != null && !where.isBlank()) sb.append(" (").append(where).append(")");
            sb.append("\n");
        }

        if (events.size() >= MAX_EVENTS) {
            sb.append("\n<i>(mostrando os ").append(MAX_EVENTS).append(" próximos eventos)</i>");
        }

        return sb.toString();
    }
}
