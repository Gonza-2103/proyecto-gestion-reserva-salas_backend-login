package com.example.login.controller;

import com.example.login.dto.AutenticacionResponseDTO;
import com.example.login.dto.LoginRequestDTO;
import com.example.login.dto.RegistroRequestDTO;
import com.example.login.service.AutenticacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 Controlador REST encargado de los endpoints públicos de autenticación:
 Recibe las peticiones HTTP enviadas desde LoginScreen y RegistroScreen (vía Retrofit)
 y delega toda la lógica de negocio a AutenticacionService
 */
@RestController
@RequestMapping("/api/auth")  //Ruta base pública configurada como permitAll() en SecurityConfig
public class AutenticacionController {

    private final AutenticacionService autenticacionService;

    //Inyección de dependencias por constructor
    public AutenticacionController(AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    /**
     Endpoint para registrar un nuevo usuario en PostgreSQL (Supabase)
     URL: POST /api/auth/register

     - @Valid activa automáticamente las validaciones del DTO (@NotBlank, @Email, @Pattern, @Size).
     - Si alguna validación falla, GlobalExceptionHandler responde automáticamente con HTTP 400.
     - Si todo sale bien, responde con HTTP 201 (Created) y el token JWT.
     */
    @PostMapping("/register")
    public ResponseEntity<AutenticacionResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO request) {
        AutenticacionResponseDTO respuesta = autenticacionService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    /**
     Endpoint para iniciar sesión validando correo, contraseña encriptada y rol seleccionado.
     URL: POST /api/auth/login

     - Si las credenciales o el rol no coinciden, GlobalExceptionHandler responde con HTTP 401 o 403.
     - Si es exitoso, responde con HTTP 200 (OK) incluyendo el token JWT y el idUsuario para Android.
     */
    @PostMapping("/login")
    public ResponseEntity<AutenticacionResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AutenticacionResponseDTO respuesta = autenticacionService.login(request);
        return ResponseEntity.ok(respuesta);
    }
}