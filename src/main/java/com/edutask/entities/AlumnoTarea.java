package com.edutask.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Alumno_Tarea")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }
}
