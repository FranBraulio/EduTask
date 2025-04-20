package com.edutask.controller;

import com.edutask.entities.Alumno;
import com.edutask.entities.Grupo;
import com.edutask.service.AlumnoService;
import com.edutask.service.ProfesorService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class AlumnoController {
    private final ProfesorService profesorService;
    private final AlumnoService alumnoService;

    public AlumnoController(ProfesorService profesorService, AlumnoService alumnoService) {
        this.profesorService = profesorService;
        this.alumnoService = alumnoService;
    }

    @PostMapping("/alumno/create")
    @Transactional
    ResponseEntity<?> create(@RequestBody Alumno alumno) {
        System.out.println(alumno.toString());
        alumnoService.saveAlumno(alumno);
        return ResponseEntity.status(HttpStatus.CREATED).body("Alumno creado con éxito");
    }

    //Metodo para sacar todos los profesores
    @GetMapping("/alumnos")
    public List<Alumno> users() {
        return alumnoService.findAll();
    }

}
