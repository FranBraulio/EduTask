package com.edutask.controller;

import com.edutask.entities.Alumno;
import com.edutask.entities.Grupo;
import com.edutask.entities.Profesor;
import com.edutask.service.AlumnoService;
import com.edutask.service.GrupoService;
import com.edutask.service.ProfesorService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@CrossOrigin("*")
public class AlumnoRestController {
    private final AlumnoService alumnoService;
    private final ProfesorService profesorService;
    private final GrupoService grupoService;

    public AlumnoRestController(AlumnoService alumnoService, ProfesorService profesorService, GrupoService grupoService) {
        this.alumnoService = alumnoService;
        this.profesorService = profesorService;
        this.grupoService = grupoService;
    }

    @PostMapping("/alumno/create")
    @Transactional
    public ResponseEntity<?> create(@RequestBody Alumno alumno) {
        alumnoService.save(alumno);
        return ResponseEntity.status(HttpStatus.CREATED).body("Alumno creado con éxito");
    }

    //Metodo para sacar todos los alumnos
    @GetMapping("/alumnos")
    public List<Alumno> users() {
        return alumnoService.findAll();
    }

    @PostMapping("/alumnos/editar")
    @Transactional
    public ResponseEntity<String> editarAlumno(@RequestBody Alumno alumno) {
        Alumno existente = alumnoService.findById(alumno.getId());
        if (existente == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado");
        }

        existente.setNombre(alumno.getNombre());
        existente.setApellido(alumno.getApellido());
        existente.setTelefono(alumno.getTelefono());
        existente.setProfesor(alumno.getProfesor());
        existente.setGrupo(alumno.getGrupo());

        alumnoService.save(existente);
        return ResponseEntity.ok("Alumno actualizado correctamente");
    }

}
