package cl.ipss.biblioteca.exception;

// Se lanza cuando no existe el libro, socio o prestamo buscado
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
