package com.example.login.config;

import com.example.login.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 Filtro de seguridad que se ejecuta una sola vez por cada petición HTTP entrante:
 Su función es revisar si la petición trae un token JWT válido en el header "Authorization"
 y, de ser así, autenticar al usuario en el contexto de Spring Security
 */
@Component // Registra el filtro como un componente administrado por Spring para inyectarlo en SecurityConfig
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;  //Encargado de validar y leer los datos del token JWT

    //Inyección de dependencias por constructor
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     Método principal del filtro que intercepta todas las peticiones antes de llegar a los controllers
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        //1. Obtiene el encabezado "Authorization" de la petición HTTP
        final String authHeader = request.getHeader("Authorization");

        //2. Si no viene el encabezado o no comienza con "Bearer ", deja pasar la petición sin autenticar (Spring Security decidirá después si la ruta es pública o si devuelve 401/403)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //3. Extrae el token puro quitando los primeros 7 caracteres correspondientes a "Bearer "
        final String token = authHeader.substring(7);

        //4. Valida la firma y la fecha de expiración del token con JwtService
        if (jwtService.esTokenValido(token) && SecurityContextHolder.getContext().getAuthentication() == null) {

            //Extrae el correo y el rol guardados dentro del token
            String correo = jwtService.extraerCorreo(token);
            String rol = jwtService.extraerRol(token);

            //Convierte el rol ("RELATOR" o "COORDINADOR") al formato de autoridad estándar de Spring ("ROLE_...")
            List<SimpleGrantedAuthority> autoridades = List.of(
                    new SimpleGrantedAuthority("ROLE_" + rol)
            );

            //Crea el objeto de autenticación reconocido por Spring Security
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    correo,  //Principal (identidad del usuario autenticado)
                    null,  //Credenciales (no se necesita la contraseña porque ya se validó el JWT)
                    autoridades  //Permisos - roles del usuario
            );

            //Adjunta detalles técnicos de la petición HTTP (IP, sesión, etc.)
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            //Registra al usuario como autenticado para el resto del ciclo de esta petición
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        //5. Continúa con el siguiente filtro de la cadena hasta llegar al Controller
        filterChain.doFilter(request, response);
    }
}