package com.sebas.biblia.carga;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "biblia.cargar", havingValue = "true")
public class CargadorRV1909 implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CargadorRV1909.class);

    private static final int TOTAL_ESPERADO = 31102;

    // {código en el archivo, nombre en español}, en el orden canónico
    private static final String[][] LIBROS = {
            {"GEN", "Génesis"}, {"EXO", "Éxodo"}, {"LEV", "Levítico"}, {"NUM", "Números"},
            {"DEU", "Deuteronomio"}, {"JOS", "Josué"}, {"JDG", "Jueces"}, {"RUT", "Rut"},
            {"1SA", "1 Samuel"}, {"2SA", "2 Samuel"}, {"1KI", "1 Reyes"}, {"2KI", "2 Reyes"},
            {"1CH", "1 Crónicas"}, {"2CH", "2 Crónicas"}, {"EZR", "Esdras"}, {"NEH", "Nehemías"},
            {"EST", "Ester"}, {"JOB", "Job"}, {"PSA", "Salmos"}, {"PRO", "Proverbios"},
            {"ECC", "Eclesiastés"}, {"SNG", "Cantares"}, {"ISA", "Isaías"}, {"JER", "Jeremías"},
            {"LAM", "Lamentaciones"}, {"EZK", "Ezequiel"}, {"DAN", "Daniel"}, {"HOS", "Oseas"},
            {"JOL", "Joel"}, {"AMO", "Amós"}, {"OBA", "Abdías"}, {"JON", "Jonás"},
            {"MIC", "Miqueas"}, {"NAM", "Nahúm"}, {"HAB", "Habacuc"}, {"ZEP", "Sofonías"},
            {"HAG", "Hageo"}, {"ZEC", "Zacarías"}, {"MAL", "Malaquías"}, {"MAT", "Mateo"},
            {"MRK", "Marcos"}, {"LUK", "Lucas"}, {"JHN", "Juan"}, {"ACT", "Hechos"},
            {"ROM", "Romanos"}, {"1CO", "1 Corintios"}, {"2CO", "2 Corintios"}, {"GAL", "Gálatas"},
            {"EPH", "Efesios"}, {"PHP", "Filipenses"}, {"COL", "Colosenses"},
            {"1TH", "1 Tesalonicenses"}, {"2TH", "2 Tesalonicenses"}, {"1TI", "1 Timoteo"},
            {"2TI", "2 Timoteo"}, {"TIT", "Tito"}, {"PHM", "Filemón"}, {"HEB", "Hebreos"},
            {"JAS", "Santiago"}, {"1PE", "1 Pedro"}, {"2PE", "2 Pedro"}, {"1JN", "1 Juan"},
            {"2JN", "2 Juan"}, {"3JN", "3 Juan"}, {"JUD", "Judas"}, {"REV", "Apocalipsis"}
    };

    // Elementos cuyo texto NO forma parte del versículo (notas, títulos, etc.)
    private static final Set<String> IGNORAR = Set.of("f", "fe", "x", "s", "h", "toc", "id");

    private static final String INSERT_LIBRO =
            "INSERT INTO libro (nombre, orden) VALUES (?, ?)";
    private static final String INSERT_VERSICULO =
            "INSERT INTO versiculo (libro_id, capitulo, numero, texto) VALUES (?, ?, ?, ?)";

    private final JdbcTemplate jdbc;
    private final String archivo;

    public CargadorRV1909(JdbcTemplate jdbc,
                          @Value("${biblia.archivo:datos/spa-rv1909.usfx.xml}") String archivo) {
        this.jdbc = jdbc;
        this.archivo = archivo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        int libros = jdbc.queryForObject("SELECT count(*) FROM libro", Integer.class);
        int versiculos = jdbc.queryForObject("SELECT count(*) FROM versiculo", Integer.class);
        if (libros > 0 || versiculos > 0) {
            log.warn("Las tablas ya tienen datos. No se carga nada.");
            return;
        }

        // 1. Insertar los 66 libros y recordar el id de cada uno
        Map<String, Integer> idsPorCodigo = new HashMap<>();
        for (int i = 0; i < LIBROS.length; i++) {
            jdbc.update(INSERT_LIBRO, LIBROS[i][1], i + 1);
            Integer id = jdbc.queryForObject(
                    "SELECT id FROM libro WHERE orden = ?", Integer.class, i + 1);
            idsPorCodigo.put(LIBROS[i][0], id);
        }

        // 2. Leer el XML y cargar los versículos por lotes
        XMLInputFactory fabrica = XMLInputFactory.newFactory();
        fabrica.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        fabrica.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

        List<Object[]> lote = new ArrayList<>();
        StringBuilder texto = new StringBuilder();
        Integer libroId = null;
        String codigoActual = null;
        int capitulo = 0;
        int numeroVersiculo = 0;
        int ignorar = 0;
        int total = 0;
        boolean dentro = false; // true solo mientras hay un versículo abierto

        try (InputStream in = Files.newInputStream(Path.of(archivo))) {
            XMLStreamReader lector = fabrica.createXMLStreamReader(in);
            try {
                while (lector.hasNext()) {
                    int evento = lector.next();

                    if (evento == XMLStreamConstants.START_ELEMENT) {
                        String nombre = lector.getLocalName();

                        // Si hay un versículo abierto y empieza algo nuevo, se cierra y se guarda
                        boolean cierra = nombre.equals("book") || nombre.equals("c")
                                || nombre.equals("v") || nombre.equals("ve");
                        if (cierra && dentro) {
                            String t = limpiar(texto);
                            if (t.isEmpty()) {
                                log.warn("Versículo sin texto en la fuente (se guarda vacío): {} {}:{}",
                                        codigoActual, capitulo, numeroVersiculo);
                            }
                            lote.add(new Object[]{libroId, capitulo, numeroVersiculo, t});
                            dentro = false;
                            if (lote.size() >= 1000) {
                                total += volcar(lote);
                            }
                        }

                        switch (nombre) {
                            case "book" -> {
                                String codigo = lector.getAttributeValue(null, "id");
                                codigoActual = codigo;
                                libroId = idsPorCodigo.get(codigo);
                                if (libroId == null) {
                                    throw new IllegalStateException("Libro desconocido: " + codigo);
                                }
                                capitulo = 0;
                            }
                            case "c" -> capitulo = numero(lector.getAttributeValue(null, "id"));
                            case "v" -> {
                                numeroVersiculo = numero(lector.getAttributeValue(null, "id"));
                                texto.setLength(0);
                                dentro = libroId != null;
                            }
                            default -> {
                                if (IGNORAR.contains(nombre)) {
                                    ignorar++;
                                }
                            }
                        }

                    } else if (evento == XMLStreamConstants.END_ELEMENT) {
                        if (IGNORAR.contains(lector.getLocalName())) {
                            ignorar--;
                        }

                    } else if (evento == XMLStreamConstants.CHARACTERS && dentro && ignorar == 0) {
                        texto.append(lector.getText());
                    }
                }
            } finally {
                lector.close();
            }
        }

        // Si el último versículo del archivo quedó abierto, se guarda también
        if (dentro) {
            String t = limpiar(texto);
            if (t.isEmpty()) {
                log.warn("Versículo sin texto en la fuente (se guarda vacío): {} {}:{}",
                        codigoActual, capitulo, numeroVersiculo);
            }
            lote.add(new Object[]{libroId, capitulo, numeroVersiculo, t});
        }

        total += volcar(lote);
        log.info("Versículos cargados: {}", total);

        if (total != TOTAL_ESPERADO) {
            throw new IllegalStateException("Se esperaban " + TOTAL_ESPERADO
                    + " versículos y se cargaron " + total + ". Se cancela la carga.");
        }
    }

    private int volcar(List<Object[]> lote) {
        if (lote.isEmpty()) {
            return 0;
        }
        jdbc.batchUpdate(INSERT_VERSICULO, lote);
        int cantidad = lote.size();
        lote.clear();
        return cantidad;
    }

    private static int numero(String id) {
        return Integer.parseInt(id.replaceAll("\\D.*$", ""));
    }

    private static String limpiar(StringBuilder texto) {
        return texto.toString().replaceAll("\\s+", " ").trim();
    }
}