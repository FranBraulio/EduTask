package com.edutask.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramService {

    private final String TELEGRAM_API_URL;
    private final RestTemplate restTemplate;

    @Autowired
    public TelegramService(@Value("${telegram.bot.token}") String botToken, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.TELEGRAM_API_URL = "https://api.telegram.org/bot" + botToken + "/sendMessage";
    }

    public void sendMessage(String chatId, String messageText) {
        Map<String, String> params = new HashMap<>();
        params.put("chat_id", chatId);
        params.put("text", messageText);

        try {
            restTemplate.postForObject(TELEGRAM_API_URL, params, String.class);
        } catch (Exception e) {
            System.err.println("Error enviando mensaje a Telegram: " + e.getMessage());
        }
    }
}

