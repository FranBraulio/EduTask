package com.edutask.controller;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administrador")
@CrossOrigin("*")
public class AdministradorController {

    @GetMapping
    public String index(Model model) {
        return "administrador";
    }
}
