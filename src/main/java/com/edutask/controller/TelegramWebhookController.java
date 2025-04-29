package com.edutask.controller;

import com.edutask.entities.Alumno;
import com.edutask.service.AlumnoService;
import com.edutask.service.TelegramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/telegram")
public class TelegramWebhookController {

    private final AlumnoService alumnoService;
    private final TelegramService telegramService;

    public TelegramWebhookController(AlumnoService alumnoService, TelegramService telegramService) {
        this.alumnoService = alumnoService;
        this.telegramService = telegramService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> onUpdateReceived(@RequestBody Map<String, Object> update) {

        // Verifica si el update tiene un mensaje
        Map<String, Object> message = (Map<String, Object>) update.get("message");
        if (message == null) {
            return ResponseEntity.ok().build();
        }

        Map<String, Object> from = (Map<String, Object>) message.get("from");
        Long chatId = ((Number) from.get("id")).longValue();
        String text = (String) message.get("text");

        // Detecta comandos tipo: /start ALUMNO_123
        if (text != null && text.startsWith("/start ALUMNO_")) {
            try {
                Long alumnoId = Long.parseLong(text.replace("/start ALUMNO_", "").trim());
                Alumno alumno = alumnoService.findById(alumnoId);

                alumno.setTelegramChatId(chatId.toString());
                alumnoService.save(alumno);

                telegramService.sendMessage(chatId.toString(), "¡Hola " + alumno.getNombre() + "! Tu cuenta ha sido vinculada con éxito a EduTask");

            } catch (Exception e) {
                telegramService.sendMessage(chatId.toString(), "Hubo un error al vincular tu cuenta.");
            }
        }

        return ResponseEntity.ok().build();
    }
}

