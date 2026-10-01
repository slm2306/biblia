package com.sebas.biblia.estudio;

import com.sebas.biblia.versiculo.Versiculo;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Estudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;

    private String explicacion;

    private LocalDateTime creadoEn;

    @ManyToMany
    @JoinTable(name = "estudio_tema",
            joinColumns = @JoinColumn(name = "estudio_id"),
            inverseJoinColumns = @JoinColumn(name = "tema_id"))
    private Set<Tema> temas = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "estudio_versiculo",
            joinColumns = @JoinColumn(name = "estudio_id"),
            inverseJoinColumns = @JoinColumn(name = "versiculo_id"))
    @OrderColumn(name = "orden")
    private List<Versiculo> versiculos = new ArrayList<>();

    @PrePersist
    void alCrear() {
        this.creadoEn = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getExplicacion() { return explicacion; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public Set<Tema> getTemas() { return temas; }
    public List<Versiculo> getVersiculos() { return versiculos; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setExplicacion(String explicacion) { this.explicacion = explicacion; }
}