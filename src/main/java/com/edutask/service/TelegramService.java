package com.edutask.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramService {

    private final String BOT_TOKEN = "7957933260:AAEDwsG4KMs1QTr2W7_Et9-sdyBDX87dtkU";
    private final String TELEGRAM_API_URL = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";

    public void sendMessage(String chatId, String messageText) {
        RestTemplate restTemplate = new RestTemplate();

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

