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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@Controller
@RequestMapping("/alumno")
@CrossOrigin("*")
public class AlumnoController {
    private final AlumnoService alumnoService;
    private final ProfesorService profesorService;
    private final GrupoService grupoService;

    public AlumnoController(AlumnoService alumnoService, ProfesorService profesorService, GrupoService grupoService) {
        this.alumnoService = alumnoService;
        this.profesorService = profesorService;
        this.grupoService = grupoService;
    }

    @GetMapping("/edit/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Alumno alumno = alumnoService.findById(id);
        if (alumno == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado");
        }

        List<Profesor> profesores = profesorService.findAll();
        List<Grupo> grupos = grupoService.findAll();

        model.addAttribute("alumno", alumno);
        model.addAttribute("profesores", profesores);
        model.addAttribute("grupos", grupos);

        return "editarAlumno";
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<String> deleteAlumno(@PathVariable("id") Long id) {
        alumnoService.deleteById(id);
        return ResponseEntity.ok("Alumno eliminado correctamente");
    }


}
