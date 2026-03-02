package com.guidev.googlecalendaragent.telegram;

import com.guidev.googlecalendaragent.client.AgnoClient;
import com.guidev.googlecalendaragent.client.GoogleCalendarClient;
import com.guidev.googlecalendaragent.client.TelegramClient;
import com.guidev.googlecalendaragent.dto.agentDTO.AgentResponse;
import com.guidev.googlecalendaragent.dto.messageDTO.Message;
import com.guidev.googlecalendaragent.dto.telegramDTO.TelegramGetResponse;
import com.guidev.googlecalendaragent.dto.telegramDTO.TelegramUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class TelegramPollingService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TelegramPollingService.class);

    private static final long ERROR_DELAY_MS = 1500L;
    private static final String DEFAULT_TZ = "America/Sao_Paulo";

    private final TelegramClient telegramClient;
    private final AgnoClient agnoClient;
    private final GoogleCalendarClient googleCalendarClient;
    private final boolean enabled;
    private final int timeoutSeconds;

    private Long offset = null;

    public TelegramPollingService(
            TelegramClient telegramClient,
            AgnoClient agnoClient,
            GoogleCalendarClient googleCalendarClient,
            @Value("${telegram.polling.enabled}") boolean enabled,
            @Value("${telegram.polling.timeout-seconds}") int timeoutSeconds
    ) {
        this.telegramClient = telegramClient;
        this.agnoClient = agnoClient;
        this.googleCalendarClient = googleCalendarClient;
        this.enabled = enabled;
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("Telegram polling is disabled");
            return;
        }

        log.info("Starting Telegram polling with timeout={}s", timeoutSeconds);
        Thread.startVirtualThread(this::pollLoop);
    }

    private void pollLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                pollOnce();
            } catch (Exception e) {
                log.error("Error during polling", e);
                sleepQuietly(ERROR_DELAY_MS);
            }
        }
    }

    private void pollOnce() {
        TelegramGetResponse response = telegramClient.getUpdates(offset, timeoutSeconds);
        if (response == null || !response.ok() || response.result() == null || response.result().isEmpty()) {
            return;
        }

        for (TelegramUpdate update : response.result()) {
            updateOffset(update);
            handleUpdate(update);
        }
    }

    private void updateOffset(TelegramUpdate update) {
        if (update.updateId() != null) {
            offset = update.updateId() + 1;
        }
    }

    private void handleUpdate(TelegramUpdate update) {
        Message message = update.message();
        if (message == null || message.chat() == null) return;

        String text = message.text();
        if (text == null || text.isBlank()) return;

        long chatId = message.chat().id();
        String userId = (message.from() != null) ? String.valueOf(message.from().id()) : String.valueOf(chatId);

        log.debug("Processing message updateId={}, userId={}, chatId={}", update.updateId(), userId, chatId);

        AgentResponse agent = agnoClient.interpret(userId, DEFAULT_TZ, text.trim());

        String reply = buildReply(agent, chatId);
        if (reply != null && !reply.isBlank()) {
            telegramClient.sendMessage(chatId, reply);
        }
    }

    private String buildReply(AgentResponse response, long chatId) {
        if (response == null) return "Agno não respondeu.";

        String action = (response.action() != null) ? response.action() : "UNKNOWN";

        return switch (action) {
            case "LIST_TODAY" -> buildTodayEventsMessage();
            case "HELP" -> extractArg(response, "text", "Ajuda não disponível.");
            case "ASK_CLARIFY" -> extractArg(response, "clarify_question", "Não entendi.");
            default -> "Entendi a intenção: %s (confiança=%.2f)".formatted(action, response.confidence());
        };
    }

    private String extractArg(AgentResponse response, String key, String defaultValue) {
        if (response.args() == null) return defaultValue;
        Object v = response.args().get(key);
        return (v == null) ? defaultValue : String.valueOf(v);
    }

    private String buildTodayEventsMessage() {
        var events = googleCalendarClient.listToday(DEFAULT_TZ);

        if (events.isEmpty()) {
            return "Hoje tá limpo. Zero compromissos. 👌";
        }

        ZoneId zone = ZoneId.of(DEFAULT_TZ);

        StringBuilder sb = new StringBuilder("📅 Compromissos de hoje:\n");
        for (var ev : events) {
            String title = (ev.getSummary() == null || ev.getSummary().isBlank()) ? "(sem título)" : ev.getSummary();

            String when;
            var start = ev.getStart();
            if (start != null && start.getDateTime() != null) {
                Instant inst = Instant.ofEpochMilli(start.getDateTime().getValue());
                String hhmm = ZonedDateTime.ofInstant(inst, zone).toLocalTime().toString();
                when = hhmm.length() >= 5 ? hhmm.substring(0, 5) : hhmm;
            } else if (start != null && start.getDate() != null) {
                when = "Dia inteiro";
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

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}