package com.edutask.controller;

import com.edutask.entities.Profesor;
import com.edutask.service.ProfesorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/administrador")
@CrossOrigin("*")
public class AdministradorController {
    private final ProfesorService profesorService;

    public AdministradorController(ProfesorService profesorService) {
        this.profesorService = profesorService;
    }

    @GetMapping
    public String index(Model model) {
        List<Profesor> profesores = profesorService.findAll();
        model.addAttribute("profesores", profesores);

        return "administrador";
    }
}
