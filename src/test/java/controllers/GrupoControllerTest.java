package controllers;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

import com.edutask.controller.GrupoController;
import com.edutask.controller.GrupoRestController;
import com.edutask.entities.Alumno;
import com.edutask.entities.Grupo;
import com.edutask.entities.Profesor;
import com.edutask.service.AlumnoService;
import com.edutask.service.GrupoService;
import com.edutask.service.ProfesorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

public class GrupoControllerTest {

    @InjectMocks
    private GrupoController grupoController;

    @InjectMocks
    private GrupoRestController grupoRestController;

    @Mock
    private GrupoService grupoService;
    @Mock
    private ProfesorService profesorService;
    @Mock
    private AlumnoService alumnoService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenGrupo_whenGrupoRestControllerCreateGrupo_shouldSaveGrupoAndReturnCreatedResponse() {
        Grupo grupo = new Grupo();

        ResponseEntity<?> response = grupoRestController.create(grupo);

        verify(grupoService).saveGrupo(grupo);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("Grupo creado con éxito");
    }

    @Test
    void givenGruposExist_whenGrupoRestControllerGetAllGrupos_shouldReturnListOfGrupos() {
        List<Grupo> grupos = List.of(new Grupo(), new Grupo());
        when(grupoService.findAll()).thenReturn(grupos);

        List<Grupo> result = grupoRestController.users();

        assertThat(result).isEqualTo(grupos);
        verify(grupoService).findAll();
    }

    @Test
    void givenExistingGrupo_whenGrupoRestControllerEditarGrupo_shouldUpdateAndReturnOkResponse() {
        Grupo input = new Grupo();
        input.setId(1L);
        input.setNombre("Nuevo nombre");
        input.setProfesor(new Profesor());

        Grupo existente = new Grupo();
        existente.setId(1L);

        when(grupoService.findById(1L)).thenReturn(existente);

        ResponseEntity<String> response = grupoRestController.editarGrupo(input);

        verify(grupoService).saveGrupo(existente);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Grupo actualizado correctamente");
        assertThat(existente.getNombre()).isEqualTo("Nuevo nombre");
        assertThat(existente.getProfesor()).isEqualTo(input.getProfesor());
    }

    @Test
    void givenNonExistentGrupo_whenGrupoRestControllerEditarGrupo_shouldThrowNotFoundException() {
        Grupo input = new Grupo();
        input.setId(99L);

        when(grupoService.findById(99L)).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> grupoRestController.editarGrupo(input));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getReason()).isEqualTo("Grupo no encontrado");
    }

    @Test
    void givenValidGrupoId_whenGrupoRestControllerMostrarFormularioEdicion_shouldReturnViewAndPopulateModel() {
        Long id = 1L;
        Grupo grupo = new Grupo();
        grupo.setId(id);

        List<Profesor> profesores = List.of(new Profesor());
        List<Alumno> alumnos = List.of(new Alumno());

        when(grupoService.findById(id)).thenReturn(grupo);
        when(profesorService.findAll()).thenReturn(profesores);
        when(alumnoService.findAll()).thenReturn(alumnos);

        Model model = mock(Model.class);

        String viewName = grupoController.mostrarFormularioEdicion(id, model);

        assertThat(viewName).isEqualTo("editarGrupo");
        verify(model).addAttribute("grupo", grupo);
        verify(model).addAttribute("profesores", profesores);
        verify(model).addAttribute("alumnos", alumnos);
    }

    @Test
    void givenNonExistentGrupoId_whenGrupoRestControllerMostrarFormularioEdicion_shouldThrowNotFoundException() {
        Long id = 99L;
        when(grupoService.findById(id)).thenReturn(null);
        Model model = mock(Model.class);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> grupoController.mostrarFormularioEdicion(id, model));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getReason()).isEqualTo("Grupo no encontrado");
    }

    @Test
    void givenGrupoId_whenGrupoRestControllerDeleteGrupo_shouldDeleteGrupoAndReturnOkResponse() {
        Long id = 1L;

        ResponseEntity<String> response = grupoController.deleteGrupo(id);

        verify(grupoService).deleteGrupo(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Grupo eliminado correctamente");
    }

}

