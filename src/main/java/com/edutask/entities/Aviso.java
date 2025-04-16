package com.edutask.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Aviso")
@Getter
@Setter
public class Aviso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    @Column(name = "canal", nullable = false)
    private String canal;

    @ManyToOne
    @JoinColumn(name = "Profesor_id", nullable = false)
    private Profesor profesor;
}
