package cl.ipss.biblioteca.config;

import cl.ipss.biblioteca.model.Libro;
import cl.ipss.biblioteca.model.Socio;
import cl.ipss.biblioteca.repository.LibroRepository;
import cl.ipss.biblioteca.repository.SocioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

// Carga datos de ejemplo, solo cuando el perfil activo es "dev"
@Configuration
@Profile("dev")
public class DatosDemo {

    @Bean
    CommandLineRunner cargarDatos(LibroRepository libros, SocioRepository socios) {
        return args -> {
            libros.save(new Libro("Cien años de soledad", "Gabriel García Márquez", "978-0-06-088328-7", 2));
            libros.save(new Libro("La casa de los espíritus", "Isabel Allende", "978-0-553-38380-6", 1));
            libros.save(new Libro("Veinte poemas de amor", "Pablo Neruda", "978-956-13-0001-0", 3));
            socios.save(new Socio("Ana Soto", "ana.soto@example.com"));
        };
    }
}
