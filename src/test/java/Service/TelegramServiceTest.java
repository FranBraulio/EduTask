package Service;

import com.edutask.service.TelegramService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TelegramServiceTest {

    private RestTemplate restTemplate;
    private TelegramService telegramService;
    private final String botToken = "TEST_BOT_TOKEN";
    private final String telegramUrl = "https://api.telegram.org/bot" + botToken + "/sendMessage";

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        telegramService = new TelegramService(botToken, restTemplate);
    }

    @Test
    void givenValidChatIdAndMessage_whenSendMessage_thenCallTelegramApi() {
        String chatId = "123456789";
        String message = "Hola desde el test";

        telegramService.sendMessage(chatId, message);

        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
        verify(restTemplate, times(1)).postForObject(eq(telegramUrl), captor.capture(), eq(String.class));

        Map<String, String> body = captor.getValue();
        assertThat(body.get("chat_id")).isEqualTo(chatId);
        assertThat(body.get("text")).isEqualTo(message);

    }

    @Test
    void givenTelegramApiFails_whenSendMessage_thenHandleException() {
        String chatId = "123456789";
        String message = "Mensaje que falla";

        doThrow(new RuntimeException("Simulated error")).when(restTemplate)
            .postForObject(eq(telegramUrl), any(), eq(String.class));

        telegramService.sendMessage(chatId, message);

        verify(restTemplate).postForObject(eq(telegramUrl), any(), eq(String.class));
    }
}

