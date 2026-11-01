# Biblioteca - Evaluacion Unidad 1 (Spring Boot)

Proyecto de la asignatura Desarrollo de Software Web II (IF205).

API REST para una biblioteca. Permite buscar libros, consultar la disponibilidad,
registrar prestamos y registrar devoluciones.

# Integrantes

- Sofia Benavente Brantes
- Mixiu Perez Iriarte

## Tecnologias

- Java 17 o superior
- Spring Boot 3.4.5
- Maven
- Spring Data JPA (H2 en desarrollo y pruebas, PostgreSQL en produccion)
- Spring Security

## Como abrir el proyecto en Visual Studio Code

1. Instalar el JDK 17 o superior.
2. Instalar las extensiones "Extension Pack for Java" y "Spring Boot Extension Pack".
3. Abrir la carpeta `biblioteca` desde File > Open Folder (el archivo `pom.xml` debe quedar en la raiz).
4. Esperar a que VS Code descargue las dependencias de Maven (se ve abajo a la derecha).

## Como ejecutar

Desde VS Code:

1. Abrir la vista "Run and Debug".
2. Elegir "Biblioteca (dev)" y presionar el boton de iniciar.

Tambien se puede abrir `BibliotecaApplication.java` y presionar "Run" sobre el metodo `main`.

Por terminal (requiere tener Maven instalado):

    mvn spring-boot:run

La aplicacion queda en http://localhost:8080

## Usuarios de prueba (perfil dev)

| Usuario       | Clave             | Rol           |
|---------------|-------------------|---------------|
| bibliotecario | dev-bibliotecario | BIBLIOTECARIO |
| socio         | dev-socio         | SOCIO         |

## Endpoints

| Metodo | Ruta                                | Descripcion                | Rol                  |
|--------|-------------------------------------|----------------------------|----------------------|
| GET    | /api/libros?q=texto                 | Buscar libros              | SOCIO, BIBLIOTECARIO |
| GET    | /api/libros/{id}/disponibilidad     | Ver disponibilidad         | SOCIO, BIBLIOTECARIO |
| POST   | /api/libros                         | Crear un libro             | BIBLIOTECARIO        |
| POST   | /api/prestamos                      | Registrar un prestamo      | SOCIO, BIBLIOTECARIO |
| POST   | /api/prestamos/{id}/devolucion      | Registrar una devolucion   | SOCIO, BIBLIOTECARIO |

## Ejemplos con curl

    curl -u socio:dev-socio "http://localhost:8080/api/libros?q=cien"
    curl -u socio:dev-socio http://localhost:8080/api/libros/1/disponibilidad
    curl -u socio:dev-socio -H "Content-Type: application/json" -d '{"libroId":1,"socioId":1}' http://localhost:8080/api/prestamos
    curl -u socio:dev-socio -X POST http://localhost:8080/api/prestamos/1/devolucion

## Pruebas

Desde VS Code: abrir la vista "Testing" y ejecutar `BibliotecaApiTest`.

Por terminal:

    mvn test

## Ambientes (perfiles)

| Perfil | Base de datos | Uso                          |
|--------|---------------|------------------------------|
| dev    | H2 en memoria | Desarrollo (perfil por defecto) |
| test   | H2 en memoria | Pruebas                      |
| prod   | PostgreSQL    | Produccion, con HTTPS        |

Para cambiar de perfil: `--spring.profiles.active=test` o `--spring.profiles.active=prod`.

En produccion se necesitan estas variables de entorno: `DB_URL`, `DB_USER`, `DB_PASSWORD`,
`SSL_KEYSTORE_PATH`, `SSL_KEYSTORE_PASSWORD`, `BIBLIOTECARIO_PASSWORD` y `SOCIO_PASSWORD`.

## Estructura del proyecto

    src/main/java/cl/ipss/biblioteca
        controller   Endpoints de la API
        service      Logica de negocio
        repository   Acceso a la base de datos
        model        Entidades (Libro, Socio, Prestamo)
        config       Seguridad y datos de ejemplo
        exception    Manejo de errores
    src/main/resources   Archivos application.yml por ambiente
    src/test             Pruebas automaticas
