package com.sebas.biblia.libro;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity

public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer id;

    private  String nombre;

    private Integer orden;

    public Integer getId() {return id;}
    public  String getNombre() {return nombre;}
    public Integer getOrden() {return  orden;}

    public  void setId(Integer id) {this.id = id;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public void setOrden(Integer orden) {this.orden = orden;}

}
