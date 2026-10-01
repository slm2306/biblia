package com.sebas.biblia.estudio;

import com.sebas.biblia.versiculo.VersiculoDto;
import java.time.LocalDateTime;
import java.util.List;

public record EstudioDto(Integer id, String titulo, String explicacion,
                         List<String> temas, List<VersiculoDto> versiculos,
                         LocalDateTime creadoEn) {

    public static EstudioDto desde(Estudio e) {
        return new EstudioDto(
                e.getId(),
                e.getTitulo(),
                e.getExplicacion(),
                e.getTemas().stream().map(Tema::getNombre).sorted().toList(),
                e.getVersiculos().stream().map(VersiculoDto::desde).toList(),
                e.getCreadoEn());
    }
}