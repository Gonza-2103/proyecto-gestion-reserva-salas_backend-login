package com.example.login.service;

import com.example.login.dto.ActualizarUsuarioRequestDTO;
import com.example.login.dto.UsuarioResponseDTO;
import com.example.login.exception.CorreoDuplicadoException;
import com.example.login.exception.RecursoNoEncontradoException;
import com.example.login.model.RolUsuario;
import com.example.login.model.Usuario;
import com.example.login.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 Servicio encargado de las operaciones CRUD de usuarios (crear, leer, actualizar y eliminar):
 UsuarioResponseDTO para no exponer contraseñas encriptadas.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;  //Encriptador BCrypt para cambios de contraseña

    // Inyección de dependencias por constructor
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     Obtiene la lista completa de todos los usuarios registrados en la base de datos
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     Obtiene únicamente los usuarios que tienen la cuenta activa (activo = true)
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarActivos() {
        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     Obtiene los usuarios filtrados por un rol específico (RELATOR o COORDINADOR)
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     Busca un usuario por su ID. Si no existe, lanza 404 Not Found
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un usuario con el ID: " + id
                ));
        return convertirADTO(usuario);
    }

    /**
     Actualiza los datos de un usuario existente (operación PUT del CRUD):
     Permite mantener la contraseña actual si el campo password viene vacío o nulo
     */
    @Transactional
    public UsuarioResponseDTO actualizarUsuario(Long id, ActualizarUsuarioRequestDTO request) {

        //1. Verifica que el usuario a modificar exista en BD
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un usuario con el ID: " + id
                ));

        //2. Normaliza nombre y correo
        String nombreLimpio = request.getNombre().trim();
        String correoLimpio = request.getCorreo().trim().toLowerCase();

        //3. Verifica que el correo no esté siendo usado por OTRO usuario distinto a este ID
        if (usuarioRepository.existsByCorreoIgnoreCaseAndIdNot(correoLimpio, id)) {
            throw new CorreoDuplicadoException(
                    "El correo " + correoLimpio + " ya pertenece a otra cuenta registrada"
            );
        }

        //4. Actualiza los campos básicos
        usuario.setNombre(nombreLimpio);
        usuario.setCorreo(correoLimpio);
        usuario.setRol(request.getRol());

        //5. Actualiza el estado activo solo si se envió en el JSON
        if (request.getActivo() != null) {
            usuario.setActivo(request.getActivo());
        }

        //6. Actualiza la contraseña solo si el usuario envió una nueva (y la encripta con BCrypt)
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        //7. Guarda los cambios en PostgreSQL y devuelve el DTO actualizado
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertirADTO(usuarioActualizado);
    }

    /**
     Elimina un usuario de la base de datos por su ID (operación DELETE del CRUD):
     Si el ID no existe, lanza 404 Not Found.
     */
    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(
                    "No se encontró un usuario con el ID: " + id
            );
        }
        usuarioRepository.deleteById(id);
    }

    /**
     Método auxiliar privado que transforma una entidad Usuario en UsuarioResponseDTO,
     asegurando que el campo password nunca salga hacia el cliente.
     */
    private UsuarioResponseDTO convertirADTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getActivo(),
                usuario.getFechaCreacion()
        );
    }
}