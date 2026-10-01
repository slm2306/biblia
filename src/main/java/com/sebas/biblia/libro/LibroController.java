package com.sebas.biblia.libro;

import com.sebas.biblia.versiculo.VersiculoRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroRepository libroRepository;
    private final VersiculoRepository versiculoRepository;

    public LibroController(LibroRepository libroRepository, VersiculoRepository versiculoRepository) {
        this.libroRepository = libroRepository;
        this.versiculoRepository = versiculoRepository;
    }

    @GetMapping
    public List<LibroDto> listar() {
        Map<Integer, Integer> capitulos = new HashMap<>();
        for (Object[] fila : versiculoRepository.capitulosPorLibro()) {
            capitulos.put((Integer) fila[0], (Integer) fila[1]);
        }
        return libroRepository.findAllByOrderByOrdenAsc().stream()
                .map(l -> new LibroDto(l.getId(), l.getNombre(), l.getOrden(),
                        capitulos.getOrDefault(l.getId(), 0)))
                .toList();
    }
}