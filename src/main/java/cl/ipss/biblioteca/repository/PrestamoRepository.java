package cl.ipss.biblioteca.repository;

import cl.ipss.biblioteca.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repositorio de prestamos
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    // Prestamos de un socio que todavia no se han devuelto
    List<Prestamo> findBySocioIdAndFechaDevolucionIsNull(Long socioId);
}
