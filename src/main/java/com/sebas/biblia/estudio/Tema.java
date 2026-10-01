package com.sebas.biblia.estudio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Tema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }
}