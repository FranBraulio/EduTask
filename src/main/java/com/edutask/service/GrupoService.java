package com.edutask.service;

import com.edutask.entities.Grupo;
import com.edutask.repository.GrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrupoService {

    @Autowired
    GrupoRepository grupoRepository;

    public Grupo saveGrupo(Grupo grupo) {
        return grupoRepository.save(grupo);
    }

    public List<Grupo> findAll() {
        return grupoRepository.findAll();
    }

    public Grupo findById(Long id) {
        return grupoRepository.findById(id).get();
    }

    public void deleteGrupo(Long id) {
        grupoRepository.deleteById(id);
    }

}
