package com.guidev.googlecalendaragent.service;

import com.guidev.googlecalendaragent.client.AgnoClient;
import com.guidev.googlecalendaragent.client.TelegramClient;
import com.guidev.googlecalendaragent.dto.agentDTO.AgentResponse;
import com.guidev.googlecalendaragent.dto.messageDTO.Message;
import com.guidev.googlecalendaragent.dto.telegramDTO.TelegramUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Responsável pelo processamento de mensagens do Telegram.
 * Extrai dados da mensagem, envia para o Agno e responde ao usuário.
 */
@Service
public class TelegramMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(TelegramMessageHandler.class);

    private final TelegramClient telegramClient;
    private final AgnoClient agnoClient;
    private final AgentActionDispatcher actionDispatcher;
    private final String defaultTimezone;

    public TelegramMessageHandler(
            TelegramClient telegramClient,
            AgnoClient agnoClient,
            AgentActionDispatcher actionDispatcher,
            @Value("${app.default-timezone:America/Sao_Paulo}") String defaultTimezone
    ) {
        this.telegramClient = telegramClient;
        this.agnoClient = agnoClient;
        this.actionDispatcher = actionDispatcher;
        this.defaultTimezone = defaultTimezone;
    }

    /**
     * Processa um update do Telegram.
     * Extrai a mensagem, interpreta via Agno e envia resposta.
     */
    public void handle(TelegramUpdate update) {
        Message message = update.message();
        if (message == null || message.chat() == null) {
            log.debug("Update {} ignored: no message or chat", update.updateId());
            return;
        }

        String text = message.text();
        if (text == null || text.isBlank()) {
            log.debug("Update {} ignored: empty text", update.updateId());
            return;
        }

        long chatId = message.chat().id();
        String userId = extractUserId(message, chatId);

        log.debug("Processing message updateId={}, userId={}, chatId={}", update.updateId(), userId, chatId);

        try {
            AgentResponse agentResponse = agnoClient.interpret(userId, defaultTimezone, text.trim());
            String reply = actionDispatcher.dispatch(agentResponse);

            if (reply != null && !reply.isBlank()) {
                telegramClient.sendMessage(chatId, reply);
            }
        } catch (Exception e) {
            log.error("Error processing message for chatId={}", chatId, e);
            telegramClient.sendMessage(chatId, "Desculpe, ocorreu um erro ao processar sua mensagem.");
        }
    }

    private String extractUserId(Message message, long chatId) {
        return (message.from() != null)
                ? String.valueOf(message.from().id())
                : String.valueOf(chatId);
    }
}

