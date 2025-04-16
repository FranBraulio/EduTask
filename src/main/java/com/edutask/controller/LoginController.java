package com.edutask.controller;

import com.edutask.service.ProfesorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private final ProfesorService profesorService;

    public LoginController(ProfesorService profesorService) {
        this.profesorService = profesorService;
    }


    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("profesores", profesorService.findAll());
        return "index";
    }
}
