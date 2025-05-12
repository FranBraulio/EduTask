package controllers;

import com.edutask.controller.AvisoController;
import com.edutask.entities.Alumno;
import com.edutask.entities.Aviso;
import com.edutask.entities.Grupo;
import com.edutask.entities.Profesor;
import com.edutask.service.AvisoService;
import com.edutask.service.AlumnoService;
import com.edutask.service.TelegramService;
import com.edutask.service.ProfesorService;
import com.edutask.service.GrupoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

class AvisoControllerTest {

    @Mock
    private AlumnoService alumnoService;
    @Mock
    private GrupoService grupoService;
    @Mock
    private ProfesorService profesorService;
    @Mock
    private TelegramService telegramService;
    @Mock
    private AvisoService avisoService;

    @InjectMocks
    private AvisoController avisoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidData_whenCreateAviso_thenReturnSuccess() {
        Map<String, String> datos = new HashMap<>();
        datos.put("mensaje_aviso", "Este es un aviso importante");
        datos.put("enviar_a_aviso", "2001");
        datos.put("profesorId", "1");
        datos.put("enviar_por", "Telegram");

        Profesor profesor = new Profesor();
        when(profesorService.findById(1L)).thenReturn(profesor);

        Alumno alumno = new Alumno();
        alumno.setTelegramChatId("123456");
        when(alumnoService.findById(2001L)).thenReturn(alumno);

        ResponseEntity<String> response = avisoController.crearAviso(datos);

        verify(avisoService, times(1)).save(any(Aviso.class));
        verify(telegramService, times(1)).sendMessage(eq("123456"), eq("AVISO: Este es un aviso importante"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Aviso creado exitosamente");
    }

    @Test
    void givenInvalidProfesorId_whenCreateAviso_thenReturnNotFoundHttpStatus() {
        Map<String, String> datos = new HashMap<>();
        datos.put("mensaje_aviso", "Este es un aviso importante");
        datos.put("enviar_a_aviso", "2001");
        datos.put("profesorId", "9999");
        datos.put("enviar_por", "Telegram");

        when(profesorService.findById(9999L)).thenReturn(null);

        ResponseEntity<String> response = avisoController.crearAviso(datos);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Profesor no encontrado");
    }

    @Test
    void givenInvalidGrupoId_whenCreateAviso_thenReturnNotFoundHttpStatus() {
        Map<String, String> datos = new HashMap<>();
        datos.put("mensaje_aviso", "Este es un aviso importante");
        datos.put("enviar_a_aviso", "5");
        datos.put("profesorId", "1");
        datos.put("enviar_por", "Telegram");

        when(profesorService.findById(1L)).thenReturn(new Profesor());
        when(grupoService.findById(5001L)).thenReturn(null);

        ResponseEntity<String> response = avisoController.crearAviso(datos);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Grupo no encontrado");
    }

    @Test
    void givenInvalidAlumnoId_whenCreateAviso_thenReturnNotFoundHttpStatus() {
        Map<String, String> datos = new HashMap<>();
        datos.put("mensaje_aviso", "Este es un aviso importante");
        datos.put("enviar_a_aviso", "3001");
        datos.put("profesorId", "1");
        datos.put("enviar_por", "Telegram");

        when(profesorService.findById(1L)).thenReturn(new Profesor());
        when(alumnoService.findById(3001L)).thenReturn(null);

        ResponseEntity<String> response = avisoController.crearAviso(datos);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Alumno no encontrado");
    }
}

