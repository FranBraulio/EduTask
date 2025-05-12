package Service;

import com.edutask.service.TelegramService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TelegramServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TelegramService telegramService;

    @Test
    void givenValidChatIdAndMessage_whenSendMessage_thenCallTelegramApi() {
        String chatId = "123456789";
        String message = "Hola desde el test";
        String url = "https://api.telegram.org/bot7957933260:AAEDwsG4KMs1QTr2W7_Et9-sdyBDX87dtkU/sendMessage";

        telegramService.sendMessage(chatId, message);

        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
        verify(restTemplate, times(1)).postForObject(eq(url), captor.capture(), eq(String.class));

        Map<String, String> body = captor.getValue();
        assertThat(body.get("chat_id")).isEqualTo(chatId);
        assertThat(body.get("text")).isEqualTo(message);
    }

    @Test
    void givenTelegramApiFails_whenSendMessage_thenHandleException() {
        String chatId = "123456789";
        String message = "Mensaje que falla";
        String url = "https://api.telegram.org/bot7957933260:AAEDwsG4KMs1QTr2W7_Et9-sdyBDX87dtkU/sendMessage";

        doThrow(new RuntimeException("Simulated error")).when(restTemplate)
            .postForObject(eq(url), any(), eq(String.class));

        telegramService.sendMessage(chatId, message);

        verify(restTemplate).postForObject(eq(url), any(), eq(String.class));
    }
}

