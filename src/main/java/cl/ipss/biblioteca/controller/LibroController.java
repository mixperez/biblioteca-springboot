package cl.ipss.biblioteca.controller;

import cl.ipss.biblioteca.model.Libro;
import cl.ipss.biblioteca.service.LibroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

// Endpoints para trabajar con libros
@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService service;

    public LibroController(LibroService service) {
        this.service = service;
    }

    // Buscar libros por titulo o autor: GET /api/libros?q=texto
    @GetMapping
    public List<Libro> buscar(@RequestParam(required = false) String q) {
        return service.buscar(q);
    }

    // Consultar disponibilidad: GET /api/libros/{id}/disponibilidad
    @GetMapping("/{id}/disponibilidad")
    public Map<String, Object> disponibilidad(@PathVariable Long id) {
        Libro libro = service.obtener(id);
        return Map.of(
                "libro", libro.getTitulo(),
                "disponible", libro.estaDisponible(),
                "ejemplaresDisponibles", libro.getEjemplaresDisponibles());
    }

    // Crear un libro: POST /api/libros (solo el bibliotecario, ver SecurityConfig)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Libro crear(@Valid @RequestBody Libro libro) {
        return service.crear(libro);
    }
}
