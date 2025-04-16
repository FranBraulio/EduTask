package com.edutask.controller;

import com.edutask.entities.Profesor;
import com.edutask.service.ProfesorService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@RestController
@CrossOrigin("*")
public class ProfesorController {

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_REGEX);

    private final PasswordEncoder passwordEncoder;
    private final ProfesorService profesorService;

    public ProfesorController(PasswordEncoder passwordEncoder, ProfesorService profesorService) {
        this.passwordEncoder = passwordEncoder;
        this.profesorService = profesorService;
    }

    @PostMapping("/register")
    @Transactional
    ResponseEntity<?> create(@RequestBody Profesor profesor) {
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

                    //setWalletUsuarios(profesor, walletService, cryptoService, walletCryptoService);
                    //emailService.enviarCorreo(profesor.getEmail(), "¡Bienvenido!", "Gracias por registrarte en CryptoSandbox.");
                    return ResponseEntity.status(HttpStatus.CREATED).body("Profesor creado con éxito");
                }else {
                    return ResponseEntity.badRequest().body("Contraseña invalida");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el profesor");
            }
        }
    }
}
