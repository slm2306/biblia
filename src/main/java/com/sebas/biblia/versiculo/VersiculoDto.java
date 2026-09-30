package com.sebas.biblia.versiculo;

public record VersiculoDto(Integer id, String libro, Integer capitulo, Integer numero, String texto) {

    public static VersiculoDto desde(Versiculo v) {
        return new VersiculoDto(v.getId(), v.getLibro().getNombre(), v.getCapitulo(), v.getNumero(), v.getTexto());
    }
}