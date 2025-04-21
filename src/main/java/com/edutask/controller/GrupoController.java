package com.edutask.controller;

import com.edutask.entities.Alumno;
import com.edutask.entities.Grupo;
import com.edutask.service.AlumnoService;
import com.edutask.service.GrupoService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin("*")
public class GrupoController {

    private final GrupoService grupoService;

    public GrupoController(GrupoService grupoService) {
        this.grupoService = grupoService;
    }

    @PostMapping("/grupo/create")
    @Transactional
    ResponseEntity<?> create(@RequestBody Grupo grupo) {
        grupoService.saveGrupo(grupo);
        return ResponseEntity.status(HttpStatus.CREATED).body("Grupo creado con éxito");
    }

    //Metodo para sacar todos los grupos
    @GetMapping("/grupos")
    public List<Grupo> users() {
        return grupoService.findAll();
    }

}
