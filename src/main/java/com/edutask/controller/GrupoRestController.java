package com.edutask.controller;

import com.edutask.entities.Alumno;
import com.edutask.entities.Grupo;
import com.edutask.service.GrupoService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@CrossOrigin("*")
public class GrupoRestController {

    private final GrupoService grupoService;

    public GrupoRestController(GrupoService grupoService) {
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

    @PostMapping("/grupo/editar")
    @Transactional
    public ResponseEntity<String> editarGrupo(@RequestBody Grupo grupo) {
        Grupo existente = grupoService.findById(grupo.getId());
        if (existente == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado");
        }

        existente.setNombre(grupo.getNombre());
        existente.setProfesor(grupo.getProfesor());

        grupoService.saveGrupo(existente);
        return ResponseEntity.ok("Grupo actualizado correctamente");
    }


}
