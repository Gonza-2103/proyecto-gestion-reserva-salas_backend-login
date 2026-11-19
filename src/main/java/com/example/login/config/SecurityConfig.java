package com.example.login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 Configuración principal de Spring Security para el microservicio de login:
 Define la política sin sesiones (STATELESS), las reglas de acceso por ruta,
 el encriptador BCrypt y los permisos CORS para la app móvil Android
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    //Inyección del filtro JWT por constructor
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     Bean de encriptación de contraseñas usando el algoritmo BCrypt:
     Se inyecta automáticamente en AutenticacionService y UsuarioService
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     Configura la cadena de filtros de seguridad HTTP para toda la API REST
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //Habilita CORS usando la configuración definida en corsConfigurationSource()
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                //Deshabilita CSRF porque las APIs REST con JWT no usan cookies de sesión
                .csrf(AbstractHttpConfigurer::disable)

                //Configura el servidor en modo STATELESS: no guarda sesiones en memoria, cada petición se valida con su JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                //Define qué rutas son públicas y cuáles requieren un token JWT válido
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  //Permite acceso libre a login y registro (el usuario aún no tiene token)
                        .requestMatchers("/error").permitAll()  //Permite acceso libre al endpoint de error interno de Spring
                        .anyRequest().authenticated()  //Cualquier otra ruta (como /api/usuarios/**) exige estar autenticado con JWT
                )

                //Inserta nuestro filtro JWT antes del filtro estándar de usuario/contraseña de Spring
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     Configuración global de CORS (Cross-Origin Resource Sharing).
     Permite que la app Android, emuladores, Postman o clientes web consuman los endpoints sin bloqueos.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        //Permite peticiones desde cualquier origen
        configuration.setAllowedOriginPatterns(List.of("*"));

        //Métodos HTTP permitidos para el login y el CRUD completo
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        //Encabezados permitidos (incluye Authorization para el token Bearer y Content-Type para JSON)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        //Permite que el cliente lea el encabezado Authorization en caso de necesitarlo
        configuration.setExposedHeaders(List.of("Authorization"));

        //Aplica esta configuración CORS a todas las rutas del microservicio
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
