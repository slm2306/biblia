package com.sebas.biblia.versiculo;

import com.sebas.biblia.libro.Libro;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Versiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id")
    private Libro libro;

    private Integer capitulo;

    private Integer numero;

    private String texto;

    public Integer getId() { return id; }
    public Libro getLibro() { return libro; }
    public Integer getCapitulo() { return capitulo; }
    public Integer getNumero() { return numero; }
    public String getTexto() { return texto; }

    public void setId(Integer id) { this.id = id; }
    public void setLibro(Libro libro) { this.libro = libro; }
    public void setCapitulo(Integer capitulo) { this.capitulo = capitulo; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public void setTexto(String texto) { this.texto = texto; }
}