package com.guidev.googlecalendaragent.telegram;

import com.guidev.googlecalendaragent.client.AgnoClient;
import com.guidev.googlecalendaragent.client.TelegramClient;
import com.guidev.googlecalendaragent.dto.agentDTO.AgentResponse;
import com.guidev.googlecalendaragent.dto.telegramDTO.TelegramGetResponse;
import com.guidev.googlecalendaragent.dto.telegramDTO.TelegramUpdate;
import com.guidev.googlecalendaragent.dto.messageDTO.Message;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TelegramPoolingService implements ApplicationRunner {

    private final TelegramClient telegramClient;
    private final boolean enabled;
    private final int timeoutSeconds;
    private Long offset = null;
    private final AgnoClient agnoClient;

    public TelegramPoolingService(
            TelegramClient telegramClient,
            @Value("${telegram.polling.enabled}") boolean enabled,
            @Value("${telegram.polling.interval}") int timeoutSeconds,
            AgnoClient agnoClient
    ) {
        this.telegramClient = telegramClient;
        this.enabled = enabled;
        this.timeoutSeconds = timeoutSeconds;
        this.agnoClient = agnoClient;
    }

    @Override
    public void run(@NonNull ApplicationArguments args) {
        if (!enabled) return;

        Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    pollOnce();
                } catch (Exception e) {
                    e.printStackTrace();
                    try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                }
            }
        });
    }

    private void pollOnce() {
        TelegramGetResponse resp = telegramClient.getUpdates(offset, timeoutSeconds);
        if (resp == null || !resp.ok() || resp.result() == null || resp.result().isEmpty()) return;

        for (TelegramUpdate upd : resp.result()) {
            if (upd.update_id() != null) offset = upd.update_id() + 1;

            Message msg = upd.message();
            if (msg == null || msg.chat() == null) continue;

            String text = msg.text();
            if (text == null || text.isBlank()) continue;

            long chatId = msg.chat().id();

            AgentResponse agent = agnoClient.interpret(
                    String.valueOf(msg.from() != null ? msg.from().id() : chatId),
                    "America/Sao_Paulo",
                    text
            );

            String reply = formatReply(agent);
            telegramClient.sendMessage(chatId, reply);
        }
    }

    private String formatReply(AgentResponse agent) {
        if (agent == null) return "Agno não respondeu (null).";

        String action = agent.action() == null ? "UNKNOWN" : agent.action();
        double conf = agent.confidence();

        return switch (action) {
            case "LIST_TODAY" -> "Entendi: listar agenda de HOJE. (stub)";
            case "HELP" -> String.valueOf(agent.args().getOrDefault("text", "Ajuda não disponível."));
            case "ASK_CLARIFY" -> String.valueOf(agent.args().getOrDefault("clarify_question", "Não entendi."));
            default -> "Entendi a intenção: " + action + " (conf=" + conf + ")";
        };
    }
}
