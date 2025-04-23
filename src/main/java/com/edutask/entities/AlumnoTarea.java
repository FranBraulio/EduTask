package com.edutask.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Alumno_Tarea")
@Getter
@Setter
public class AlumnoTarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Alumno_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "Tarea_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Tarea tarea;
}
