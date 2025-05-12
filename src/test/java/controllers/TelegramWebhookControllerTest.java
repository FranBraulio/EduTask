package controllers;

import com.edutask.controller.TelegramWebhookController;
import com.edutask.entities.Alumno;
import com.edutask.service.AlumnoService;
import com.edutask.service.TelegramService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class TelegramWebhookControllerTest {

    @Mock
    private AlumnoService alumnoService;

    @Mock
    private TelegramService telegramService;

    @InjectMocks
    private TelegramWebhookController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenStartCommand_whenAlumnoExists_shouldLinkTelegramAndSendConfirmation() {
        Long alumnoId = 123L;
        Long chatId = 456789L;

        Alumno alumno = new Alumno();
        alumno.setId(alumnoId);
        alumno.setNombre("Juan");

        Map<String, Object> update = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        Map<String, Object> from = new HashMap<>();

        from.put("id", chatId);
        message.put("from", from);
        message.put("text", "/start ALUMNO_" + alumnoId);
        update.put("message", message);

        when(alumnoService.findById(alumnoId)).thenReturn(alumno);

        ResponseEntity<?> response = controller.onUpdateReceived(update);

        verify(alumnoService).save(alumno);
        verify(telegramService).sendMessage(chatId.toString(), "¡Hola Juan! Tu cuenta ha sido vinculada con éxito a EduTask");
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void givenStartCommand_whenAlumnoNotFound_shouldSendErrorMessage() {
        Long alumnoId = 999L;
        Long chatId = 456789L;

        Map<String, Object> update = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        Map<String, Object> from = new HashMap<>();

        from.put("id", chatId);
        message.put("from", from);
        message.put("text", "/start ALUMNO_" + alumnoId);
        update.put("message", message);

        when(alumnoService.findById(alumnoId)).thenThrow(new RuntimeException("Alumno no encontrado"));

        ResponseEntity<?> response = controller.onUpdateReceived(update);

        verify(telegramService).sendMessage(chatId.toString(), "Hubo un error al vincular tu cuenta.");
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void givenUpdateWithoutMessage_shouldReturnOkWithoutProcessing() {
        Map<String, Object> update = new HashMap<>();

        ResponseEntity<?> response = controller.onUpdateReceived(update);

        verifyNoInteractions(alumnoService, telegramService);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}

