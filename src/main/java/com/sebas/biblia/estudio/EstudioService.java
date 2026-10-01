package com.sebas.biblia.estudio;

import com.sebas.biblia.versiculo.Versiculo;
import com.sebas.biblia.versiculo.VersiculoRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class EstudioService {

    private final EstudioRepository estudioRepository;
    private final TemaRepository temaRepository;
    private final VersiculoRepository versiculoRepository;

    public EstudioService(EstudioRepository estudioRepository,
                          TemaRepository temaRepository,
                          VersiculoRepository versiculoRepository) {
        this.estudioRepository = estudioRepository;
        this.temaRepository = temaRepository;
        this.versiculoRepository = versiculoRepository;
    }

    public EstudioDto crear(EstudioCrearDto dto) {
        String titulo = dto.titulo().trim();
        if (estudioRepository.existsByTituloIgnoreCase(titulo)) {
            throw tituloRepetido(titulo);
        }
        Estudio estudio = new Estudio();
        estudio.setTitulo(titulo);
        estudio.setExplicacion(dto.explicacion().trim());
        estudio.getTemas().addAll(resolverTemas(dto.temas()));
        estudio.getVersiculos().addAll(resolverPasajes(dto.pasajes()));
        return EstudioDto.desde(estudioRepository.save(estudio));
    }

    @Transactional(readOnly = true)
    public List<EstudioResumenDto> listar() {
        return estudioRepository.findAllByOrderByCreadoEnDesc()
                .stream()
                .map(EstudioResumenDto::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public EstudioDto obtener(Integer id) {
        return EstudioDto.desde(buscar(id));
    }
    @Transactional(readOnly = true)
    public List<EstudioDto> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escribe una palabra para buscar");
        }
        List<Integer> ids = estudioRepository.buscarIds(texto.trim());
        Map<Integer, Estudio> porId = estudioRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Estudio::getId, Function.identity()));
        return ids.stream().map(porId::get).map(EstudioDto::desde).toList();
    }

    // Reemplaza título, explicación, temas y pasajes por los nuevos
    public EstudioDto actualizar(Integer id, EstudioCrearDto dto) {
        Estudio estudio = buscar(id);
        String titulo = dto.titulo().trim();
        if (estudioRepository.existsByTituloIgnoreCaseAndIdNot(titulo, id)) {
            throw tituloRepetido(titulo);
        }
        estudio.setTitulo(titulo);
        estudio.setExplicacion(dto.explicacion().trim());

        List<Tema> temas = resolverTemas(dto.temas());
        List<Versiculo> versiculos = resolverPasajes(dto.pasajes());

        estudio.getTemas().clear();
        estudio.getTemas().addAll(temas);
        estudio.getVersiculos().clear();
        estudio.getVersiculos().addAll(versiculos);

        return EstudioDto.desde(estudio);
    }

    public void eliminar(Integer id) {
        if (!estudioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El estudio no existe");
        }
        estudioRepository.deleteById(id);
    }

    private Estudio buscar(Integer id) {
        return estudioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El estudio no existe"));
    }

    private ResponseStatusException tituloRepetido(String titulo) {
        return new ResponseStatusException(HttpStatus.CONFLICT,
                "Ya existe un estudio con el título: " + titulo);
    }

    // Normaliza los temas (minúsculas, sin repetidos) y crea los que no existan
    private List<Tema> resolverTemas(List<String> nombres) {
        if (nombres == null) {
            return List.of();
        }
        Set<String> normalizados = new LinkedHashSet<>();
        for (String n : nombres) {
            if (n != null && !n.isBlank()) {
                normalizados.add(n.trim().toLowerCase());
            }
        }
        List<Tema> temas = new ArrayList<>();
        for (String nombre : normalizados) {
            Tema tema = temaRepository.findByNombre(nombre).orElseGet(() -> {
                Tema nuevo = new Tema();
                nuevo.setNombre(nombre);
                return temaRepository.save(nuevo);
            });
            temas.add(tema);
        }
        return temas;
    }

    // Convierte cada pasaje (libro, capítulo, desde, hasta) en sus versículos
    private List<Versiculo> resolverPasajes(List<PasajeDto> pasajes) {
        List<Versiculo> resultado = new ArrayList<>();
        Set<Integer> yaAgregados = new HashSet<>();

        for (PasajeDto p : pasajes) {
            int hasta = p.hasta() != null ? p.hasta() : p.desde();
            String referencia = p.libro() + " " + p.capitulo() + ":" + p.desde() + "-" + hasta;

            if (hasta < p.desde()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Rango inválido en " + referencia);
            }

            List<Versiculo> encontrados = versiculoRepository
                    .findByLibroNombreIgnoreCaseAndCapituloAndNumeroBetweenOrderByNumero(
                            p.libro().trim(), p.capitulo(), p.desde(), hasta);

            if (encontrados.size() != hasta - p.desde() + 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No se encontró el pasaje completo: " + referencia
                                + " (revisa el nombre del libro, con tildes)");
            }

            for (Versiculo v : encontrados) {
                if (yaAgregados.add(v.getId())) {
                    resultado.add(v);
                }
            }
        }
        return resultado;
    }
}