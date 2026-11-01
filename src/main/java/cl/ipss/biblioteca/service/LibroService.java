package cl.ipss.biblioteca.service;

import cl.ipss.biblioteca.exception.RecursoNoEncontradoException;
import cl.ipss.biblioteca.model.Libro;
import cl.ipss.biblioteca.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// Logica de negocio relacionada con los libros
@Service
public class LibroService {

    private final LibroRepository libros;

    // Spring entrega el repositorio por el constructor (inyeccion de dependencias)
    public LibroService(LibroRepository libros) {
        this.libros = libros;
    }

    // Si no se escribe texto devuelve todos los libros
    public List<Libro> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return libros.findAll();
        }
        return libros.findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase(texto, texto);
    }

    public Libro obtener(Long id) {
        return libros.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado: " + id));
    }

    public Libro crear(Libro libro) {
        return libros.save(libro);
    }
}
