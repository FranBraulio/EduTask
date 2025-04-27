package com.edutask.service;

import com.edutask.entities.AlumnoTarea;
import com.edutask.repository.AlumnoTareaRepository;
import com.edutask.repository.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlumnoTareaService {
    @Autowired
    private AlumnoTareaRepository alumnoTareaRepository;

    public AlumnoTarea save(AlumnoTarea alumnoTarea) {
        return alumnoTareaRepository.save(alumnoTarea);
    }
}
