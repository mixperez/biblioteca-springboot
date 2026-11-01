package cl.ipss.biblioteca.controller;

import cl.ipss.biblioteca.model.Prestamo;
import cl.ipss.biblioteca.service.PrestamoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

// Endpoints para prestamos y devoluciones
@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService service;

    public PrestamoController(PrestamoService service) {
        this.service = service;
    }

    // DTO de entrada: datos que se envian para pedir un prestamo
    public record SolicitudPrestamo(@NotNull Long libroId, @NotNull Long socioId) {
    }

    // DTO de salida: datos que se devuelven al cliente
    public record RespuestaPrestamo(Long id, String libro, String socio,
                                    LocalDate fechaPrestamo, LocalDate fechaLimite, LocalDate fechaDevolucion) {

        // Convierte un Prestamo en su respuesta
        static RespuestaPrestamo de(Prestamo p) {
            return new RespuestaPrestamo(p.getId(), p.getLibro().getTitulo(), p.getSocio().getNombre(),
                    p.getFechaPrestamo(), p.getFechaLimite(), p.getFechaDevolucion());
        }
    }

    // Registrar un prestamo: POST /api/prestamos
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RespuestaPrestamo prestar(@Valid @RequestBody SolicitudPrestamo solicitud) {
        return RespuestaPrestamo.de(service.prestar(solicitud.libroId(), solicitud.socioId()));
    }

    // Registrar una devolucion: POST /api/prestamos/{id}/devolucion
    @PostMapping("/{id}/devolucion")
    public RespuestaPrestamo devolver(@PathVariable Long id) {
        return RespuestaPrestamo.de(service.devolver(id));
    }
}
