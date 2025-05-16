package controllers;

import com.edutask.controller.TareaController;
import com.edutask.entities.*;
import com.edutask.service.*;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class TareaControllerTest {

    @Mock
    private TareaService tareaService;
    @Mock
    private AlumnoService alumnoService;
    @Mock
    private GrupoService grupoService;
    @Mock
    private AlumnoTareaService alumnoTareaService;
    @Mock
    private ProfesorService profesorService;
    @Mock
    private TelegramService telegramService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private TareaController tareaController;

    private Alumno alumno;
    private Grupo grupo;
    private Profesor profesor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        alumno = new Alumno();
        alumno.setId(1L);
        alumno.setNombre("Juan");
        alumno.setTelegramChatId("12345");

        grupo = new Grupo();
        grupo.setId(1L);
        grupo.setAlumnos(new ArrayList<Alumno>());
        grupo.getAlumnos().add(alumno);

        profesor = new Profesor();
        profesor.setId(1L);
    }

    @Test
    void givenValidTareaData_whenCreateTarea_shouldReturnSuccess() throws MessagingException {
        Map<String, String> tareaData = new HashMap<>();
        tareaData.put("descripcion", "Tarea de prueba");
        tareaData.put("fecha_limite", "2025-06-01");
        tareaData.put("asignar_a", "1");
        tareaData.put("profesorId", "1");

        when(profesorService.findById(1L)).thenReturn(profesor);
        when(grupoService.findById(1L)).thenReturn(grupo);

        ResponseEntity<String> response = tareaController.crearTarea(tareaData);

        verify(tareaService).saveTarea(any(Tarea.class));
        verify(telegramService).sendMessage("12345", "Buenas, te han asignado la siguiente tarea: Tarea de prueba. Recuerda que tienes de fecha limite: 2025-06-01");
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo("Tarea creada exitosamente");
    }

    @Test
    void givenTareaData_whenAlumnoNotFound_shouldReturnError() throws MessagingException {
        Map<String, String> tareaData = new HashMap<>();
        tareaData.put("descripcion", "Tarea de prueba");
        tareaData.put("fecha_limite", "2025-06-01");
        tareaData.put("asignar_a", "9999");
        tareaData.put("profesorId", "1");

        when(profesorService.findById(1L)).thenReturn(profesor);
        when(alumnoService.findById(9999L)).thenReturn(null);

        ResponseEntity<String> response = tareaController.crearTarea(tareaData);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Alumno no encontrado");
    }


    @Test
    void givenValidTareaData_whenDeleteTarea_shouldReturnSuccess() {
        Long tareaId = 1L;

        ResponseEntity<String> response = tareaController.deleteTarea(tareaId);

        verify(tareaService).deleteById(tareaId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Tarea eliminada correctamente");
    }

    @Test
    void givenNoAlumnosAssigned_whenCreateTarea_shouldSendMessageOnlyToTelegram() throws MessagingException {
        Map<String, String> tareaData = new HashMap<>();
        tareaData.put("descripcion", "Tarea sin alumnos asignados");
        tareaData.put("fecha_limite", "");
        tareaData.put("asignar_a", "1001");
        tareaData.put("profesorId", "1");

        when(profesorService.findById(1L)).thenReturn(profesor);
        when(alumnoService.findById(1001L)).thenReturn(alumno);

        ResponseEntity<String> response = tareaController.crearTarea(tareaData);

        verify(telegramService).sendMessage("12345", "Buenas, te han asignado la siguiente tarea: Tarea sin alumnos asignados");
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}

