package com.guidev.googlecalendaragent.service;

import com.google.api.services.calendar.model.Event;
import com.guidev.googlecalendaragent.dto.agentDTO.AgentResponse;
import com.guidev.googlecalendaragent.client.GoogleCalendarClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
        if (events == null || events.isEmpty()) {
            return "Não há próximos compromissos. 👌";
        }

        // Verifica se todos os eventos caem no mesmo dia
        ZoneId zone = ZoneId.of(defaultTimezone);
        boolean allSameDay = allEventsOnSameDay(events, zone);

        if (allSameDay) {
            LocalDate eventDate = extractEventDate(events.get(0), zone);
            String dateFormatted = eventDate != null
                    ? eventDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "hoje";
            return formatEventList(
                    "📅 Agenda do dia " + dateFormatted + ":\n",
                    "Não há próximos compromissos. 👌",
                    events,
                    false
            );
        }

        return formatEventList("📅 Próximos compromissos:\n", "Não há próximos compromissos. 👌", events, true);
    }

    public String buildWeekEventsMessage() {
        var events = googleCalendarClient.listWeek(defaultTimezone);
        return formatEventList("📅 Compromissos desta semana:\n", "Nenhum compromisso esta semana. 👌", events, true);
    }

    public String buildNextWeekEventsMessage() {
        var events = googleCalendarClient.listNextWeek(defaultTimezone);
        return formatEventList("📅 Compromissos da próxima semana:\n", "Não há compromissos para a próxima semana. 👌", events, true);
    }

    public String buildMonthEventsMessage() {
        var events = googleCalendarClient.listMonth(defaultTimezone);
        return formatEventList("📅 Compromissos deste mês:\n", "Nenhum compromisso este mês. 👌", events, true);
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

    /**
     * Verifica se todos os eventos da lista caem no mesmo dia.
     */
    private boolean allEventsOnSameDay(List<Event> events, ZoneId zone) {
        if (events == null || events.size() <= 1) return true;

        LocalDate firstDate = extractEventDate(events.getFirst(), zone);
        if (firstDate == null) return false;

        return events.stream()
                .map(ev -> extractEventDate(ev, zone))
                .allMatch(firstDate::equals);
    }

    /**
     * Extrai a data local de um evento.
     */
    private LocalDate extractEventDate(Event event, ZoneId zone) {
        var start = event.getStart();
        if (start == null) return null;

        if (start.getDateTime() != null) {
            Instant inst = Instant.ofEpochMilli(start.getDateTime().getValue());
            return ZonedDateTime.ofInstant(inst, zone).toLocalDate();
        }
        if (start.getDate() != null) {
            return LocalDate.parse(start.getDate().toStringRfc3339());
        }
        return null;
    }

    private String formatEventTime(String startStr, String endStr) {
        try {
            var start = java.time.LocalDateTime.parse(startStr);
            String dateStr = start.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String startTime = start.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

            if (endStr != null) {
                var end = java.time.LocalDateTime.parse(endStr);
                String endTime = end.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                return dateStr + " das " + startTime + " às " + endTime;
            }
            return dateStr + " às " + startTime;
        } catch (Exception e) {
            return startStr;
        }
    }

    /**
     * Remove caracteres indesejados (como - e :) do início e fim do título do evento.
     */
    private String sanitizeTitle(String title) {
        return title.replaceAll("^[\\-:\\s]+", "").replaceAll("[\\-:\\s]+$", "").trim();
    }

    private static final int MAX_EVENTS = 10;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String formatEventList(String header, String emptyMsg, List<Event> events, boolean showDate) {
        if (events == null || events.isEmpty()) {
            return emptyMsg;
        }

        ZoneId zone = ZoneId.of(defaultTimezone);
        StringBuilder sb = new StringBuilder("<b>").append(header).append("</b>\n");

        int limit = Math.min(events.size(), MAX_EVENTS);
        for (int i = 0; i < limit; i++) {
            var ev = events.get(i);
            String title = (ev.getSummary() == null || ev.getSummary().isBlank()) ? "(sem título)" : sanitizeTitle(ev.getSummary());
            String when;
            var start = ev.getStart();

            if (start != null && start.getDateTime() != null) {
                Instant inst = Instant.ofEpochMilli(start.getDateTime().getValue());
                ZonedDateTime zdt = ZonedDateTime.ofInstant(inst, zone);
                String hhmm = zdt.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

                if (showDate) {
                    when = zdt.toLocalDate().format(DATE_FMT) + " às " + hhmm;
                } else {
                    when = hhmm;
                }
            } else if (start != null && start.getDate() != null) {
                if (showDate) {
                    LocalDate d = LocalDate.parse(start.getDate().toStringRfc3339());
                    when = d.format(DATE_FMT);
                } else {
                    when = "Dia inteiro";
                }
            } else {
                when = "Sem horário";
            }

            String where = ev.getLocation();
            String meet = ev.getHangoutLink();

            sb.append("✅ ").append(when).append(" — <b>").append(title).append("</b>");
            if (meet != null && !meet.isBlank()) sb.append(" 📎 <i>Meet</i>");
            else if (where != null && !where.isBlank()) sb.append(" 📍 <i>").append(where).append("</i>");

            // Linha em branco entre eventos (exceto após o último)
            if (i < limit - 1) {
                sb.append("\n\n");
            } else {
                sb.append("\n");
            }
        }

        if (events.size() > MAX_EVENTS) {
            sb.append("\n<i>📋 Mostrando os ").append(MAX_EVENTS).append(" próximos eventos de ").append(events.size()).append(" no total.</i>");
        }

        return sb.toString();
    }
}
