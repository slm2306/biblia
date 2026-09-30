package com.sebas.biblia.versiculo;

public record VersiculoDto(String libro, Integer capitulo, Integer numero, String texto) {

    public static VersiculoDto desde(Versiculo v) {
        return new VersiculoDto(v.getLibro().getNombre(), v.getCapitulo(), v.getNumero(), v.getTexto());
    }
}