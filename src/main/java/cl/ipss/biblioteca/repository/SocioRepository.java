package cl.ipss.biblioteca.repository;

import cl.ipss.biblioteca.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio de socios
public interface SocioRepository extends JpaRepository<Socio, Long> {
}
