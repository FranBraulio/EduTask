package com.edutask.service;

import com.edutask.entities.Profesor;
import com.edutask.repository.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfesorService {
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private ProfesorRepository profesorRepository;

    public ProfesorService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Profesor saveUser(Profesor user) {
        return profesorRepository.save(user);
    }
    public List<Profesor> findAll() {
        return profesorRepository.findAll();
    }
    public void deleteById(Long id) {
        profesorRepository.deleteById(id);
    }
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public Profesor findByUsername(String username) {
        return profesorRepository.findByEmail(username);
    }

    public Profesor findByEmail(String email) {
        return profesorRepository.findByEmail(email);
    }

}
