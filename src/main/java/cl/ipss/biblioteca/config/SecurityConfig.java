package cl.ipss.biblioteca.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// Configuracion de seguridad de la aplicacion
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Las contrasenas se guardan cifradas con BCrypt
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Usuarios de ejemplo guardados en memoria.
    // Las contrasenas se leen desde application-*.yml para no escribirlas en el codigo.
    @Bean
    UserDetailsService usuarios(PasswordEncoder encoder,
                                @Value("${biblioteca.seguridad.bibliotecario-password}") String passBibliotecario,
                                @Value("${biblioteca.seguridad.socio-password}") String passSocio) {
        return new InMemoryUserDetailsManager(
                User.withUsername("bibliotecario").password(encoder.encode(passBibliotecario)).roles("BIBLIOTECARIO").build(),
                User.withUsername("socio").password(encoder.encode(passSocio)).roles("SOCIO").build());
    }

    // Reglas de acceso a los endpoints
    @Bean
    SecurityFilterChain filtros(HttpSecurity http,
                                @Value("${biblioteca.seguridad.exigir-https:false}") boolean exigirHttps) throws Exception {
        http
            // La API no usa sesiones ni cookies, por eso se desactiva CSRF
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").denyAll()
                .requestMatchers(HttpMethod.GET, "/api/libros/**").hasAnyRole("SOCIO", "BIBLIOTECARIO")
                .requestMatchers(HttpMethod.POST, "/api/libros").hasRole("BIBLIOTECARIO")
                .requestMatchers("/api/prestamos/**").hasAnyRole("SOCIO", "BIBLIOTECARIO")
                // Todo lo demas queda bloqueado
                .anyRequest().denyAll())
            .httpBasic(Customizer.withDefaults())
            .headers(h -> h
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'none'"))
                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000)));

        // En produccion se obliga a usar HTTPS
        if (exigirHttps) {
            http.requiresChannel(c -> c.anyRequest().requiresSecure());
        }
        return http.build();
    }
}
