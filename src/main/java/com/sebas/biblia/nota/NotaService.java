package com.sebas.biblia.nota;

import com.sebas.biblia.versiculo.Versiculo;
import com.sebas.biblia.versiculo.VersiculoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class NotaService {

    private final NotaRepository notaRepository;
    private final VersiculoRepository versiculoRepository;

    public NotaService(NotaRepository notaRepository, VersiculoRepository versiculoRepository) {
        this.notaRepository = notaRepository;
        this.versiculoRepository = versiculoRepository;
    }

    public NotaDto crear(NotaCrearDto dto) {
        Versiculo versiculo = versiculoRepository.findById(dto.versiculoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El versículo no existe"));
        Nota nota = new Nota();
        nota.setVersiculo(versiculo);
        nota.setContenido(dto.contenido().trim());
        return NotaDto.desde(notaRepository.save(nota));
    }

    @Transactional(readOnly = true)
    public List<NotaDto> listarPorVersiculo(Integer versiculoId) {
        return notaRepository.findByVersiculoIdOrderByCreadaEnDesc(versiculoId)
                .stream()
                .map(NotaDto::desde)
                .toList();
    }

    public NotaDto actualizar(Integer id, NotaActualizarDto dto) {
        Nota nota = notaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La nota no existe"));
        nota.setContenido(dto.contenido().trim());
        return NotaDto.desde(nota);
    }

    public void eliminar(Integer id) {
        if (!notaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La nota no existe");
        }
        notaRepository.deleteById(id);
    }
}
