package com.guidev.googlecalendaragent.telegram;

import com.guidev.googlecalendaragent.client.TelegramClient;
import com.guidev.googlecalendaragent.dto.telegramDTO.TelegramGetResponse;
import com.guidev.googlecalendaragent.dto.telegramDTO.TelegramUpdate;
import com.guidev.googlecalendaragent.service.TelegramMessageHandler;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Responsável exclusivamente pelo polling de updates do Telegram.
 * Delega o processamento de mensagens ao TelegramMessageHandler.
 */
@Component
public class TelegramPollingService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TelegramPollingService.class);
    private static final long ERROR_DELAY_MS = 1500L;

    private final TelegramClient telegramClient;
    private final TelegramMessageHandler messageHandler;
    private final boolean enabled;
    private final int timeoutSeconds;

    private Long offset = null;

    public TelegramPollingService(
            TelegramClient telegramClient,
            TelegramMessageHandler messageHandler,
            @Value("${telegram.polling.enabled}") boolean enabled,
            @Value("${telegram.polling.timeout-seconds}") int timeoutSeconds
    ) {
        this.telegramClient = telegramClient;
        this.messageHandler = messageHandler;
        this.enabled = enabled;
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public void run(@NonNull ApplicationArguments args) {
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
                sleepQuietly();
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
            messageHandler.handle(update);
        }
    }

    private void updateOffset(TelegramUpdate update) {
        if (update.updateId() != null) {
            offset = update.updateId() + 1;
        }
    }


    private void sleepQuietly() {
        try {
            Thread.sleep(TelegramPollingService.ERROR_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}