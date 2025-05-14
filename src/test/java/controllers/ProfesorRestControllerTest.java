package controllers;

import com.edutask.controller.ProfesorRestController;
import com.edutask.entities.Profesor;
import com.edutask.service.ProfesorService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.security.auth.login.CredentialException;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfesorRestControllerTest {

    @InjectMocks
    private ProfesorRestController profesorRestController;

    @Mock
    private ProfesorService profesorService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Authentication authentication;
    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidProfesor_whenCreate_shouldReturnCreatedResponse() {
        // Arrange
        Profesor profesor = new Profesor();
        profesor.setUsername("test");
        profesor.setEmail("test@example.com");
        profesor.setPassword("Password123");

        when(profesorService.findByEmail("test@example.com")).thenReturn(null);
        when(passwordEncoder.encode("Password123")).thenReturn("encoded");

        // Act
        ResponseEntity<?> response = profesorRestController.create(profesor);

        // Assert
        verify(profesorService).saveUser(any(Profesor.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("Profesor creado con éxito");
    }

    @Test
    void givenExistingEmail_whenCreate_shouldReturnBadRequest() {
        Profesor profesor = new Profesor();
        profesor.setUsername("user123");
        profesor.setEmail("duplicate@example.com");
        profesor.setPassword("Password123");

        when(profesorService.findByEmail("duplicate@example.com")).thenReturn(new Profesor());

        ResponseEntity<?> response = profesorRestController.create(profesor);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("El correo ya esta registrado");
    }

    @Test
    void givenProfesorWithInvalidPassword_whenCreate_shouldReturnBadRequest() {
        Profesor profesor = new Profesor();
        profesor.setUsername("test");
        profesor.setEmail("test@example.com");
        profesor.setPassword("weak");

        when(profesorService.findByEmail("test@example.com")).thenReturn(null);

        ResponseEntity<?> response = profesorRestController.create(profesor);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Contraseña invalida");
    }

    @Test
    void givenValidCredentials_whenLogin_shouldReturnOk() {
        Profesor profesor = new Profesor();
        profesor.setEmail("test@example.com");
        profesor.setPassword("encoded");

        when(profesorService.findByEmail("test@example.com")).thenReturn(profesor);
        when(profesorService.validatePassword("Password123", "encoded")).thenReturn(true);

        ResponseEntity<?> response = profesorRestController.login("test@example.com", "Password123");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Login exitoso");
    }

    @Test
    void givenWrongPassword_whenLogin_shouldReturnUnauthorized() {
        Profesor profesor = new Profesor();
        profesor.setPassword("encoded");

        when(profesorService.findByEmail("test@example.com")).thenReturn(profesor);
        when(profesorService.validatePassword("wrong", "encoded")).thenReturn(false);

        ResponseEntity<?> response = profesorRestController.login("test@example.com", "wrong");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Contraseña incorrecta");
    }

    @Test
    void givenNonExistingUser_whenLogin_shouldReturnUnauthorized() {
        when(profesorService.findByEmail("notfound@example.com")).thenReturn(null);

        ResponseEntity<?> response = profesorRestController.login("notfound@example.com", "123");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Usuario no encontrado");
    }

    @Test
    void givenValidEditProfileRequest_whenEditProfile_shouldReturnOk() throws Exception {
        Profesor profesor = new Profesor();
        profesor.setUsername("newuser");
        profesor.setEmail("new@example.com");
        profesor.setPassword("newpass");

        Profesor existing = new Profesor();
        existing.setEmail("old@example.com");

        when(profesorService.findByUsername(anyString())).thenReturn(existing);
        when(passwordEncoder.encode("newpass")).thenReturn("encoded");

        // Mock authentication
        mockStaticSecurityContext("olduser");

        ResponseEntity<?> response = profesorRestController.editProfile(profesor);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Profesor registrado con éxito");
    }

    @Test
    void givenDuplicateEmailInEditProfile_whenEditProfile_shouldThrowException() {
        Profesor profesor = new Profesor();
        profesor.setEmail("duplicate@example.com");

        Profesor loggedUser = new Profesor();
        loggedUser.setEmail("old@example.com");

        Profesor duplicate = new Profesor();
        duplicate.setEmail("duplicate@example.com");

        when(profesorService.findByUsername(anyString())).thenReturn(loggedUser);
        when(profesorService.findAll()).thenReturn(List.of(duplicate));

        mockStaticSecurityContext("olduser");

        assertThatThrownBy(() -> profesorRestController.editProfile(profesor))
            .isInstanceOf(CredentialException.class)
            .hasMessage("El correo ya está registrado");
    }

    @Test
    void givenId_whenDeleteUserById_shouldRedirect() throws IOException, MessagingException {
        Long id = 1L;

        profesorRestController.deleteUserById(id, response);

        verify(profesorService).deleteById(id);
        verify(response).sendRedirect("/administrador");
    }

    // helper para mockear seguridad
    private void mockStaticSecurityContext(String username) {
        var context = mock(org.springframework.security.core.context.SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        org.springframework.security.core.context.SecurityContextHolder.setContext(context);
    }
}

