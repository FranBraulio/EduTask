package com.edutask.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Tarea")
@Getter
@Setter
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mensaje")
    private String mensaje;

    @Column(name = "fecha_fin")
    private LocalDateTime fecha_fin;

    @ManyToOne
    @JoinColumn(name = "Profesor_id", nullable = false)
    private Profesor profesor;

    @OneToMany(mappedBy = "Tarea", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Alumno_Tarea> alumnoTarea = new HashSet<>();
}
