package com.example.login.controller;

import com.example.login.dto.ActualizarUsuarioRequestDTO;
import com.example.login.dto.UsuarioResponseDTO;
import com.example.login.model.RolUsuario;
import com.example.login.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 Controlador REST encargado de las operaciones CRUD de usuarios:
 Todas estas rutas están protegidas en SecurityConfig: requieren enviar el header
 "Authorization: Bearer <token>" obtenido previamente en /api/auth/login o /api/auth/register
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService; // Servicio que gestiona las consultas y modificaciones en BD

    //Inyección de dependencias por constructor
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     Lista todos los usuarios registrados en la base de datos (sin exponer contraseñas)
     URL: GET /api/usuarios
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     Lista únicamente los usuarios con cuenta activa (activo = true)
     URL: GET /api/usuarios/activos
     */
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponseDTO>> listarActivos() {
        List<UsuarioResponseDTO> usuariosActivos = usuarioService.listarActivos();
        return ResponseEntity.ok(usuariosActivos);
    }

    /**
     Lista los usuarios filtrados por rol (RELATOR o COORDINADOR)
     URL: GET /api/usuarios/rol/RELATOR  o  GET /api/usuarios/rol/COORDINADOR
     */
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDTO>> listarPorRol(@PathVariable RolUsuario rol) {
        List<UsuarioResponseDTO> usuariosPorRol = usuarioService.listarPorRol(rol);
        return ResponseEntity.ok(usuariosPorRol);
    }

    /**
     Obtiene el detalle de un usuario específico por su ID.
     URL: GET /api/usuarios/{id}

     - Si el ID no existe, UsuarioService lanza RecursoNoEncontradoException (HTTP 404).
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     Actualiza los datos de un usuario existente (operación PUT del CRUD)
     URL: PUT /api/usuarios/{id}

     - @Valid valida los datos enviados en ActualizarUsuarioRequestDTO antes de procesarlos.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequestDTO request) {
        UsuarioResponseDTO usuarioActualizado = usuarioService.actualizarUsuario(id, request);
        return ResponseEntity.ok(usuarioActualizado);
    }

    /**
     Elimina un usuario de la base de datos por su ID (operación DELETE del CRUD)
     URL: DELETE /api/usuarios/{id}

     - Devuelve HTTP 200 con un JSON de confirmación simple para facilitar la lectura en Retrofit y Postman.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();  //Devuelve HTTP 204 No Content confirmando que el registro fue eliminado
    }
}