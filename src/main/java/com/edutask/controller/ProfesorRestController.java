package com.edutask.controller;

import com.edutask.entities.Profesor;
import com.edutask.service.EmailService;
import com.edutask.service.ProfesorService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.CredentialException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@CrossOrigin("*")
public class ProfesorRestController {

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_REGEX);

    private final PasswordEncoder passwordEncoder;
    private final ProfesorService profesorService;
    private EmailService emailService;

    public ProfesorRestController(PasswordEncoder passwordEncoder, ProfesorService profesorService, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.profesorService = profesorService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> create(@RequestBody Profesor profesor) {
        if (profesor.getUsername() == null || profesor.getEmail() == null || profesor.getPassword() == null) {
            return ResponseEntity.badRequest().body("Faltan datos requeridos");
        } else if (profesorService.findByEmail(profesor.getEmail()) != null) {
            return ResponseEntity.badRequest().body("El correo ya esta registrado");
        }else{
            try {
                profesor.setRol("USER");
                if (pattern.matcher(profesor.getPassword()).matches()) {
                    profesor.setPassword(passwordEncoder.encode(profesor.getPassword()));
                    if (profesor.getEmail().equals("fran@gmail.com") || profesor.getEmail().equals("admin@gmail.com")) {
                        profesor.setRol("ADMIN");
                    }
                    profesorService.saveUser(profesor);
                    emailService.enviarCorreo(profesor.getEmail(), "¡Bienvenido!", "Gracias por registrarte en CryptoSandbox.");
                    return ResponseEntity.status(HttpStatus.CREATED).body("Profesor creado con éxito");
                }else {
                    return ResponseEntity.badRequest().body("Contraseña invalida");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el profesor");
            }
        }
    }

    // Metodo para iniciar sesión en el sistema
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        Profesor profesor = profesorService.findByEmail(email);
        if (profesor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        boolean isPasswordValid = profesorService.validatePassword(password, profesor.getPassword());
        if (!isPasswordValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Login exitoso");
    }

    // Metodo para obtener información del usuario autenticado
    @GetMapping("/user")
    List<String> info() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // Obtener la autenticación actual
        List<String> userInfo;
        userInfo = new ArrayList<>(Arrays.asList(profesorService.findByUsername(authentication.getName()).getUsername(), profesorService.findByUsername(authentication.getName()).getEmail(), profesorService.findByUsername(authentication.getName()).getRol(), profesorService.findByUsername(authentication.getName()).getId().toString()));
        return userInfo;
    }

    // Metodo para editar el perfil del profesor autenticado
    @PostMapping("/editProfile")
    public ResponseEntity<?> editProfile(@RequestBody Profesor profesor) throws CredentialException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Profesor loggedUser;
        loggedUser = profesorService.findByUsername(authentication.getName());

        // Verifica si el correo proporcionado ya está registrado
        if (!loggedUser.getEmail().equals(profesor.getEmail())) {
            for (Profesor usu : profesorService.findAll()) {
                if (usu.getEmail().equals(profesor.getEmail()) && !(usu == loggedUser)) {
                    throw new CredentialException("El correo ya está registrado");
                }
            }
        }
        // Actualiza los datos del profesor
        loggedUser.setUsername(profesor.getUsername());
        loggedUser.setEmail(profesor.getEmail());
        loggedUser.setPassword(passwordEncoder.encode(profesor.getPassword()));  // Encripta la nueva contraseña
        profesorService.saveUser(loggedUser);
        return ResponseEntity.ok("Profesor registrado con éxito");
    }

    // Metodo para eliminar un usuario por ID
    @GetMapping("/delete/{id}")
    public void deleteUserById(@PathVariable("id") Long id, HttpServletResponse response) throws IOException, MessagingException {
        Profesor profesor = profesorService.findById(id);
        emailService.enviarCorreo(profesor.getEmail(), "¡Cuenta eliminada!", "Su cuenta de EduTask ha sido eliminada.");
        profesorService.deleteById(id);
        response.sendRedirect("/administrador");
    }

    //Metodo para sacar todos los profesores
    @GetMapping("/users")
    public List<Profesor> users() {
        return profesorService.findAll();
    }

}
