package com.guidev.googlecalendaragent.controller;

import com.guidev.googlecalendaragent.client.TelegramClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/telegram")
public class TelegramWebhookController {

    private final String webhookSecret;
    private final TelegramClient telegramClient;
    private final long testChatId;

    public TelegramWebhookController(
            @Value("${telegram.webhook-secret}") String webhookSecret,
            @Value("${telegram.test-chat-id}") long testChatId,
            TelegramClient telegramClient
    ) {
        this.webhookSecret = webhookSecret;
        this.testChatId = testChatId;
        this.telegramClient = telegramClient;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestHeader(name = "X-Telegram-Bot-Api-Secret-Token", required = false) String secretToken,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        if (secretToken == null || !secretToken.equals(webhookSecret)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/test/send")
    public ResponseEntity<String> testSend(@RequestParam(defaultValue = "Novo Teste") String text) {
        if (testChatId == 0) {
            return ResponseEntity.badRequest().body("Chat ID de teste não configurado");
        }
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body("Texto da mensagem não pode ser vazio");
        }

        telegramClient.sendMessage(testChatId, text);
        return ResponseEntity.ok("Mensagem Enviada");
    }
}