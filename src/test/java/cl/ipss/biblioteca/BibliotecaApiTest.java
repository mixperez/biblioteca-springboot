package cl.ipss.biblioteca;

import cl.ipss.biblioteca.model.Libro;
import cl.ipss.biblioteca.model.Socio;
import cl.ipss.biblioteca.repository.LibroRepository;
import cl.ipss.biblioteca.repository.SocioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Pruebas de la API usando el perfil "test"
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BibliotecaApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    LibroRepository libros;

    @Autowired
    SocioRepository socios;

    Long libroId;
    Long socioId;

    // Antes de cada prueba se crea un libro (1 ejemplar) y un socio
    @BeforeEach
    void datos() {
        libroId = libros.save(new Libro("Test Driven", "Autor", "ISBN-" + System.nanoTime(), 1)).getId();
        socioId = socios.save(new Socio("Socio Test", "t" + System.nanoTime() + "@example.com")).getId();
    }

    // Sin usuario y clave debe responder 401
    @Test
    void sinCredencialesDevuelve401() throws Exception {
        mvc.perform(get("/api/libros")).andExpect(status().isUnauthorized());
    }

    // Un socio puede buscar libros y ver su disponibilidad
    @Test
    void socioPuedeBuscarYVerDisponibilidad() throws Exception {
        mvc.perform(get("/api/libros").param("q", "Test").with(httpBasic("socio", "test-socio")))
                .andExpect(status().isOk());

        mvc.perform(get("/api/libros/" + libroId + "/disponibilidad").with(httpBasic("socio", "test-socio")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible").value(true));
    }

    // Un socio no puede crear libros (solo el bibliotecario), debe responder 403
    @Test
    void socioNoPuedeCrearLibros() throws Exception {
        String libroJson = "{\"titulo\":\"X\",\"autor\":\"Y\",\"isbn\":\"Z\","
                + "\"ejemplaresTotales\":1,\"ejemplaresDisponibles\":1}";

        mvc.perform(post("/api/libros").with(httpBasic("socio", "test-socio"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(libroJson))
                .andExpect(status().isForbidden());
    }

    // Prueba completa: prestar, intentar prestar sin stock y devolver
    @Test
    void prestamoYDevolucionActualizanDisponibilidad() throws Exception {
        String cuerpo = "{\"libroId\":" + libroId + ",\"socioId\":" + socioId + "}";

        // Se registra el prestamo
        String respuesta = mvc.perform(post("/api/prestamos").with(httpBasic("socio", "test-socio"))
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String prestamoId = respuesta.replaceAll(".*\"id\":(\\d+).*", "$1");

        // Ya no queda ejemplar disponible
        mvc.perform(get("/api/libros/" + libroId + "/disponibilidad").with(httpBasic("socio", "test-socio")))
                .andExpect(jsonPath("$.disponible").value(false));

        // Si se pide de nuevo debe dar conflicto (409)
        mvc.perform(post("/api/prestamos").with(httpBasic("socio", "test-socio"))
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isConflict());

        // Se devuelve el libro
        mvc.perform(post("/api/prestamos/" + prestamoId + "/devolucion").with(httpBasic("socio", "test-socio")))
                .andExpect(status().isOk());

        // Vuelve a estar disponible
        mvc.perform(get("/api/libros/" + libroId + "/disponibilidad").with(httpBasic("socio", "test-socio")))
                .andExpect(jsonPath("$.disponible").value(true));
    }
}
