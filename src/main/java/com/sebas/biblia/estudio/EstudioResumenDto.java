package com.sebas.biblia.estudio;

import java.time.LocalDateTime;
import java.util.List;

public record EstudioResumenDto(Integer id, String titulo, List<String> temas, LocalDateTime creadoEn) {

    public static EstudioResumenDto desde(Estudio e) {
        return new EstudioResumenDto(
                e.getId(),
                e.getTitulo(),
                e.getTemas().stream().map(Tema::getNombre).sorted().toList(),
                e.getCreadoEn());
    }
}