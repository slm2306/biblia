package com.sebas.biblia.versiculo;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/versiculos")
public class VersiculoController {

    private final VersiculoRepository versiculoRepository;

    public VersiculoController(VersiculoRepository versiculoRepository) {
        this.versiculoRepository = versiculoRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<VersiculoDto> consultar(@RequestParam String libro, @RequestParam Integer capitulo) {
        return versiculoRepository
                .findByLibroNombreAndCapituloOrderByNumero(libro, capitulo)
                .stream()
                .map(VersiculoDto::desde)
                .toList();
    }
}