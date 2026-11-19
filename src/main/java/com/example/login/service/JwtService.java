package com.example.login.service;

import com.example.login.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 Servicio encargado de generar, leer y validar los tokens JWT (JSON Web Tokens):
 Utiliza el algoritmo HS256 y la clave secreta configurada en application.properties
 */
@Service
public class JwtService {

    //Inyecta el valor de "jwt.secret" definido en application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    //Inyecta el tiempo de expiración en milisegundos definido en "jwt.expiration" (ej: 86400000 = 24h)
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     Convierte el texto de jwt.secret en una clave criptográfica válida para HS256:
     Requiere al menos 32 caracteres (256 bits) para que JJWT no lance WeakKeyException
     */
    private SecretKey obtenerClaveFirma() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     Genera un token JWT firmado para un usuario autenticado:
     Incluye información útil (claims) como idUsuario, nombre y rol
     */
    public String generarToken(Usuario usuario) {

        //Claims personalizados que viajarán dentro del token (nunca incluir la contraseña)
        Map<String, Object> claims = new HashMap<>();
        claims.put("idUsuario", usuario.getId());
        claims.put("nombre", usuario.getNombre());
        claims.put("rol", usuario.getRol().name());

        Date ahora = new Date();  //Fecha y hora actual de emisión
        Date fechaExpiracion = new Date(ahora.getTime() + jwtExpiration);  //Fecha límite de validez

        //Construye y firma el token con el algoritmo HS256
        return Jwts.builder()
                .setClaims(claims)  //Agrega los datos personalizados
                .setSubject(usuario.getCorreo())  //Identificador principal del dueño del token
                .setIssuedAt(ahora)  // Cuándo se emitió el token
                .setExpiration(fechaExpiracion)  //Cuándo vence el token
                .signWith(obtenerClaveFirma(), SignatureAlgorithm.HS256)  //Firma digital con la clave secreta
                .compact();  //Convierte el token a su formato final de texto (header.payload.signature)
    }

    /**
     Extrae todos los datos (Claims) contenidos dentro de un token JWT:
     Si el token fue alterado o expiró, JJWT lanzará una JwtException automáticamente
     */
    public Claims extraerTodosLosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(obtenerClaveFirma()) // Verifica la firma con la misma clave secreta
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     Obtiene el correo (subject) del usuario dueño del token
     */
    public String extraerCorreo(String token) {
        return extraerTodosLosClaims(token).getSubject();
    }

    /**
     Obtiene el rol guardado dentro del token ("RELATOR" o "COORDINADOR")
     */
    public String extraerRol(String token) {
        return extraerTodosLosClaims(token).get("rol", String.class);
    }

    /**
     Verifica si un token es auténtico, está bien formado y no ha expirado:
     Retorna true si es válido, o false ante cualquier alteración o vencimiento
     */
    public boolean esTokenValido(String token) {
        try {
            Claims claims = extraerTodosLosClaims(token);
            return !claims.getExpiration().before(new Date());  //Comprueba que no esté vencido
        } catch (JwtException | IllegalArgumentException e) {
            return false;  // Firma inválida, token malformado, expirado o vacío
        }
    }
}