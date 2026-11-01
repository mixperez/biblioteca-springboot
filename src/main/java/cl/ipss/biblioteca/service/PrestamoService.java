package cl.ipss.biblioteca.service;

import cl.ipss.biblioteca.exception.RecursoNoEncontradoException;
import cl.ipss.biblioteca.model.Libro;
import cl.ipss.biblioteca.model.Prestamo;
import cl.ipss.biblioteca.model.Socio;
import cl.ipss.biblioteca.repository.PrestamoRepository;
import cl.ipss.biblioteca.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

// Logica de negocio de prestamos y devoluciones
@Service
public class PrestamoService {

    private final PrestamoRepository prestamos;
    private final SocioRepository socios;
    private final LibroService libroService;
    private final int diasPlazo;

    // Los dias de plazo se leen desde application.yml
    public PrestamoService(PrestamoRepository prestamos,
                           SocioRepository socios,
                           LibroService libroService,
                           @Value("${biblioteca.prestamo.dias-plazo}") int diasPlazo) {
        this.prestamos = prestamos;
        this.socios = socios;
        this.libroService = libroService;
        this.diasPlazo = diasPlazo;
    }

    // Registra un prestamo. Con @Transactional, si algo falla se deshacen todos los cambios.
    @Transactional
    public Prestamo prestar(Long libroId, Long socioId) {
        Libro libro = libroService.obtener(libroId);
        Socio socio = socios.findById(socioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio no encontrado: " + socioId));

        libro.prestar();
        return prestamos.save(new Prestamo(libro, socio, LocalDate.now(), diasPlazo));
    }

    // Registra la devolucion de un prestamo
    @Transactional
    public Prestamo devolver(Long prestamoId) {
        Prestamo prestamo = prestamos.findById(prestamoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Prestamo no encontrado: " + prestamoId));

        if (!prestamo.estaActivo()) {
            throw new IllegalStateException("El prestamo ya fue devuelto");
        }

        prestamo.registrarDevolucion(LocalDate.now());
        prestamo.getLibro().devolver();
        return prestamo;
    }
}
