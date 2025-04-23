package com.edutask.controller;

import com.edutask.entities.Alumno;
import com.edutask.service.AlumnoService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class AlumnoController {
    private final AlumnoService alumnoService;

    public AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @PostMapping("/alumno/create")
    @Transactional
    ResponseEntity<?> create(@RequestBody Alumno alumno) {
        alumnoService.saveAlumno(alumno);
        return ResponseEntity.status(HttpStatus.CREATED).body("Alumno creado con éxito");
    }

    //Metodo para sacar todos los alumnos
    @GetMapping("/alumnos")
    public List<Alumno> users() {
        return alumnoService.findAll();
    }

}
