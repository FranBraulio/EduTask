package com.edutask.service;

import com.edutask.entities.Tarea;
import com.edutask.repository.TareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TareaService {
    @Autowired
    TareaRepository tareaRepository;

    public Tarea saveTarea(Tarea tarea) {
        return tareaRepository.save(tarea);
    }

    public List<Tarea> findAll() {
        return tareaRepository.findAll();
    }

    public void deleteById(Long id) {
        tareaRepository.deleteById(id);
    }

}
