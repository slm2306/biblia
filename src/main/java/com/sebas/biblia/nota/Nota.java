package com.sebas.biblia.nota;

import com.sebas.biblia.versiculo.Versiculo;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;

@Entity
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "versiculo_id")
    private Versiculo versiculo;

    private String contenido;

    private LocalDateTime creadaEn;

    @PrePersist
    void alCrear() {
        this.creadaEn = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public Versiculo getVersiculo() { return versiculo; }
    public String getContenido() { return contenido; }
    public LocalDateTime getCreadaEn() { return creadaEn; }

    public void setVersiculo(Versiculo versiculo) { this.versiculo = versiculo; }
    public void setContenido(String contenido) { this.contenido = contenido; }
}