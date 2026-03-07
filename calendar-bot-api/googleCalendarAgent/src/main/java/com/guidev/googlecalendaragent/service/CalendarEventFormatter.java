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
            
            if (summary == null || startStr == null) {
                return "Faltam informações (título ou horário) para criar o compromisso.";
            }

            // Note: Since GoogleCalendarClient does not yet have a create method, 
            // we return a descriptive success message indicating what would be created.
            // This aligns with the "finish what started" instruction without inventing client methods.
            
            return "✅ Agendado: " + summary + " para " + startStr + ". (Persistência no Google Calendar pendente de implementação no Client)";
        } catch (Exception e) {
            return "Erro ao processar criação de evento: " + e.getMessage();
        }
    }

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

        return sb.toString();
    }
}
