package com.edutask.service;

import com.edutask.entities.Aviso;
import com.edutask.repository.AvisoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AvisoService {
    @Autowired
    AvisoRepository avisoRepository;

    public Aviso save(Aviso aviso) {
        return avisoRepository.save(aviso);
    }

}
