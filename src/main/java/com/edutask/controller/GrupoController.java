package com.edutask.controller;

import com.edutask.entities.Alumno;
import com.edutask.entities.Grupo;
import com.edutask.entities.Profesor;
import com.edutask.service.AlumnoService;
import com.edutask.service.GrupoService;
import com.edutask.service.ProfesorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@Controller
@RequestMapping("/grupo")
@CrossOrigin("*")
public class GrupoController {
    private final AlumnoService alumnoService;
    private final ProfesorService profesorService;
    private final GrupoService grupoService;

    public GrupoController(AlumnoService alumnoService, ProfesorService profesorService, GrupoService grupoService) {
        this.alumnoService = alumnoService;
        this.profesorService = profesorService;
        this.grupoService = grupoService;
    }

    @GetMapping("/edit/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Grupo grupo = grupoService.findById(id);
        if (grupo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado");
        }

        List<Profesor> profesores = profesorService.findAll();
        List<Alumno> alumnos = alumnoService.findAll();

        model.addAttribute("grupo", grupo);
        model.addAttribute("profesores", profesores);
        model.addAttribute("alumnos", alumnos);

        return "editarGrupo";
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<String> deleteGrupo(@PathVariable("id") Long id) {
        grupoService.deleteGrupo(id);
        return ResponseEntity.ok("Grupo eliminado correctamente");
    }


}

