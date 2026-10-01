package com.sebas.biblia.estudio;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/estudios")
public class EstudioController {

    private final EstudioService estudioService;

    public EstudioController(EstudioService estudioService) {
        this.estudioService = estudioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstudioDto crear(@Valid @RequestBody EstudioCrearDto dto) {
        return estudioService.crear(dto);
    }

    @GetMapping
    public List<EstudioResumenDto> listar() {
        return estudioService.listar();
    }

    @GetMapping("/{id}")
    public EstudioDto obtener(@PathVariable Integer id) {
        return estudioService.obtener(id);
    }
    @PutMapping("/{id}")
    public EstudioDto actualizar(@PathVariable Integer id, @Valid @RequestBody EstudioCrearDto dto) {
        return estudioService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        estudioService.eliminar(id);
    }
    @GetMapping("/buscar")
    public List<EstudioDto> buscar(@RequestParam String q) {
        return estudioService.buscar(q);
    }
}