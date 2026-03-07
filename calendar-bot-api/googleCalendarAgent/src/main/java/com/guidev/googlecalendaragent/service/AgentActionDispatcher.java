package com.guidev.googlecalendaragent.service;

import com.guidev.googlecalendaragent.dto.agentDTO.AgentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Responsável por rotear as ações interpretadas pelo Agno
 * para os handlers apropriados e construir as respostas.
 */
@Service
public class AgentActionDispatcher {

    private static final Logger log = LoggerFactory.getLogger(AgentActionDispatcher.class);

    private final CalendarEventFormatter calendarEventFormatter;

    public AgentActionDispatcher(CalendarEventFormatter calendarEventFormatter) {
        this.calendarEventFormatter = calendarEventFormatter;
    }

    /**
     * Processa a resposta do agente e retorna a mensagem apropriada.
     */
    public String dispatch(AgentResponse response) {
        if (response == null) {
            return "Agno não respondeu.";
        }

        String action = (response.action() != null) ? response.action() : "UNKNOWN";
        log.debug("Dispatching action: {} with confidence: {}", action, response.confidence());

        return switch (action) {
            case "LIST_TODAY" -> handleListToday();
            case "LIST_TOMORROW" -> handleListTomorrow();
            case "LIST_NEXT" -> handleListNext();
            case "LIST_NEXT_WEEK", "LIST_WEEK" -> handleListNextWeek();
            case "LIST_NEXT_MONTH", "LIST_MONTH" -> handleListNextMonth();
            case "HELP" -> handleHelp(response);
            case "ASK_CLARIFY" -> handleAskClarify(response);
            case "CREATE_EVENT" -> handleCreateEvent(response);
            default -> handleUnknown(action, response.confidence());
        };
    }

    private String handleListToday() {
        return calendarEventFormatter.buildTodayEventsMessage();
    }

    private String handleListTomorrow() {
        return calendarEventFormatter.buildTomorrowEventsMessage();
    }

    private String handleListNext() {
        return calendarEventFormatter.buildNextEventsMessage();
    }

    private String handleListNextWeek() {
        return calendarEventFormatter.buildNextWeekEventsMessage();
    }

    private String handleListNextMonth() {
        return calendarEventFormatter.buildNextMonthEventsMessage();
    }

    private String handleHelp(AgentResponse response) {
        return extractArg(response, "text", "Ajuda não disponível.");
    }

    private String handleAskClarify(AgentResponse response) {
        return extractArg(response, "clarify_question", "Não entendi.");
    }

    private String handleCreateEvent(AgentResponse response) {
        return calendarEventFormatter.createEvent(response);
    }

    private String handleUnknown(String action, double confidence) {
        return "Não entendi a intenção: %s (confiança=%.2f)".formatted(action, confidence);
    }

    private String extractArg(AgentResponse response, String key, String defaultValue) {
        if (response.args() == null) return defaultValue;
        Object v = response.args().get(key);
        return (v == null) ? defaultValue : String.valueOf(v);
    }
}

