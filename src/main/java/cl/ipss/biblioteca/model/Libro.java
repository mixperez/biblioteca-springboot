package cl.ipss.biblioteca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// Entidad Libro: se guarda en la tabla "libro"
@Entity
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String titulo;

    @NotBlank
    private String autor;

    // El ISBN no se puede repetir
    @NotBlank
    @Column(unique = true)
    private String isbn;

    @Min(0)
    private int ejemplaresTotales;

    @Min(0)
    private int ejemplaresDisponibles;

    // Constructor vacio que necesita JPA
    protected Libro() {
    }

    public Libro(String titulo, String autor, String isbn, int ejemplares) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.ejemplaresTotales = ejemplares;
        this.ejemplaresDisponibles = ejemplares;
    }

    // Devuelve true si queda al menos un ejemplar
    public boolean estaDisponible() {
        return ejemplaresDisponibles > 0;
    }

    // Descuenta un ejemplar al prestar
    public void prestar() {
        if (!estaDisponible()) {
            throw new IllegalStateException("No hay ejemplares disponibles de: " + titulo);
        }
        ejemplaresDisponibles--;
    }

    // Suma un ejemplar al devolver
    public void devolver() {
        if (ejemplaresDisponibles < ejemplaresTotales) {
            ejemplaresDisponibles++;
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getEjemplaresTotales() {
        return ejemplaresTotales;
    }

    public int getEjemplaresDisponibles() {
        return ejemplaresDisponibles;
    }
}
