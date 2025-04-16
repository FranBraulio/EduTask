package com.edutask.repository;

import com.edutask.entities.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
    Profesor findByEmail(String email);
}
