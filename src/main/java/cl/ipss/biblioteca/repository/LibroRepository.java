package cl.ipss.biblioteca.repository;

import cl.ipss.biblioteca.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repositorio de libros. Spring Data crea la implementacion solo.
public interface LibroRepository extends JpaRepository<Libro, Long> {

    // Busca libros cuyo titulo o autor contenga el texto (sin importar mayusculas)
    List<Libro> findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase(String titulo, String autor);
}
