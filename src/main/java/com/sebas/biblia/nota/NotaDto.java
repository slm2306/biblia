package com.sebas.biblia.nota;

import java.time.LocalDateTime;

public record NotaDto(Integer id, Integer versiculoId, String referencia,
                      String contenido, LocalDateTime creadaEn) {

    public static NotaDto desde(Nota n) {
        var v = n.getVersiculo();
        String referencia = v.getLibro().getNombre() + " " + v.getCapitulo() + ":" + v.getNumero();
        return new NotaDto(n.getId(), v.getId(), referencia, n.getContenido(), n.getCreadaEn());
    }
}