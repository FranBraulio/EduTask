package controllers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;
import com.edutask.controller.AlumnoController;
import com.edutask.controller.AlumnoRestController;
import com.edutask.entities.Alumno;
import com.edutask.entities.Grupo;
import com.edutask.entities.Profesor;
import com.edutask.service.AlumnoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class AlumnoControllerTest {

    @InjectMocks
    private AlumnoRestController alumnoRestController;
    @InjectMocks
    private AlumnoController alumnoController;

    @Mock
    private AlumnoService alumnoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenAlumno_whenAlumnoRestControllerCreate_shouldSaveAlumnoAndReturnCreatedResponse() {
        Alumno alumno = new Alumno();
        ResponseEntity<?> response = alumnoRestController.create(alumno);

        verify(alumnoService).save(alumno);
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody()).isEqualTo("Alumno creado con éxito");
    }

    @Test
    void givenAlumno_whenAlumnoRestControllerEditarAlumno_shouldUpdateAndReturnOkResponse() {
        // Arrange
        Alumno input = new Alumno();
        input.setId(1L);
        input.setNombre("Juan");
        input.setApellido("Pérez");
        input.setTelefono("123456789");
        input.setProfesor(new Profesor());
        input.setGrupo(new Grupo());

        Alumno existente = new Alumno();
        existente.setId(1L);

        when(alumnoService.findById(1L)).thenReturn(existente);
        ResponseEntity<String> response = alumnoRestController.editarAlumno(input);

        verify(alumnoService).save(existente);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo("Alumno actualizado correctamente");
        assertThat(existente.getNombre()).isEqualTo("Juan");
    }

    @Test
    void givenNonExistentAlumnoId_whenAlumnoRestControllerEditarAlumno_shouldThrowNotFoundException() {
        // Arrange
        Alumno input = new Alumno();
        input.setId(99L);

        when(alumnoService.findById(99L)).thenReturn(null);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> alumnoRestController.editarAlumno(input));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Alumno no encontrado");
    }

    @Test
    void  givenAlumno_whenAlumnoRestControllerEditarAlumno_shouldDeleteAlumnoAndReturnOkResponse() {
        Long id = 1L;

        ResponseEntity<String> response = alumnoController.deleteAlumno(id);

        verify(alumnoService).deleteById(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Alumno eliminado correctamente");
    }

}

