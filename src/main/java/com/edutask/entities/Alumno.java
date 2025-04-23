package com.edutask.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Alumno")
@Getter
@Setter
@ToString
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "telefono")
    private String telefono;

    @ManyToOne
    @JoinColumn(name = "Profesor_id")
    private Profesor profesor;

    @ManyToOne
    @JoinColumn(name = "Grupo_id")
    private Grupo grupo;

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AlumnoTarea> alumnoTarea = new HashSet<>();
}
