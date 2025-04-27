package com.edutask.controller;

import com.edutask.entities.*;
import com.edutask.repository.*;
import com.edutask.service.TareaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/tareas")
public class TareaController {

    private final TareaRepository tareaRepository;
    private final AlumnoRepository alumnoRepository;
    private final GrupoRepository grupoRepository;
    private final AlumnoTareaRepository alumnoTareaRepository;
    private final ProfesorRepository profesorRepository; // si hace falta
    private final TareaService tareaService;

    public TareaController(TareaRepository tareaRepository, AlumnoRepository alumnoRepository, GrupoRepository grupoRepository, AlumnoTareaRepository alumnoTareaRepository, ProfesorRepository profesorRepository, TareaService tareaService) {
        this.tareaRepository = tareaRepository;
        this.alumnoRepository = alumnoRepository;
        this.grupoRepository = grupoRepository;
        this.alumnoTareaRepository = alumnoTareaRepository;
        this.profesorRepository = profesorRepository;
        this.tareaService = tareaService;
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearTarea(@RequestBody Map<String, String> datos) {
        String descripcion = datos.get("descripcion");
        String fechaLimite = datos.get("fecha_limite");
        String asignarA = datos.get("asignar_a");

        Tarea tarea = new Tarea();
        tarea.setMensaje(descripcion);

        if (fechaLimite != null && !fechaLimite.isEmpty()) {
            tarea.setFecha_fin(LocalDate.parse(fechaLimite).atStartOfDay());
        }

        Profesor profesor = profesorRepository.findById(1L).orElseThrow();
        tarea.setProfesor(profesor);
        tareaService.saveTarea(tarea);

        Set<Alumno> alumnosAsignados = new HashSet<>();
        Long idAsignado = Long.parseLong(asignarA);

        if (idAsignado <= 1000) {
            Grupo grupo = grupoRepository.findById(idAsignado).orElseThrow();
            alumnosAsignados.addAll(grupo.getAlumnos());
        } else {
            Alumno alumno = alumnoRepository.findById(idAsignado).orElseThrow();
            alumnosAsignados.add(alumno);
        }
        for (Alumno alumno : alumnosAsignados) {
            AlumnoTarea alumnoTarea = new AlumnoTarea();
            alumnoTarea.setAlumno(alumno);
            alumnoTarea.setTarea(tarea);
            alumnoTareaRepository.save(alumnoTarea);
        }

        return ResponseEntity.ok("Tarea creada exitosamente");
    }

    //Metodo para sacar todas las tareas
    @GetMapping("/history")
    public List<Tarea> tareas() {
        return tareaService.findAll();
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<String> deleteTarea(@PathVariable("id") Long id) {
        tareaService.deleteById(id);
        return ResponseEntity.ok("Tarea eliminada correctamente");
    }

}

