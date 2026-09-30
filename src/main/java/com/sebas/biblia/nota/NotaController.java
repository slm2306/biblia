package com.sebas.biblia.nota;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notas")
public class NotaController {

    private final NotaService notaService;

    public NotaController(NotaService notaService) {
        this.notaService = notaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotaDto crear(@Valid @RequestBody NotaCrearDto dto) {
        return notaService.crear(dto);
    }

    @GetMapping
    public List<NotaDto> listar(@RequestParam Integer versiculoId) {
        return notaService.listarPorVersiculo(versiculoId);
    }

    @PutMapping("/{id}")
    public NotaDto actualizar(@PathVariable Integer id, @Valid @RequestBody NotaActualizarDto dto) {
        return notaService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        notaService.eliminar(id);
    }
}